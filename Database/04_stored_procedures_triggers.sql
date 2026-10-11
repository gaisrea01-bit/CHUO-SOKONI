
-- =============================================================================
-- File: 04_stored_procedures_triggers.sql
-- Description: Automated Business Logic, Margin Calculations, Triggers & Audits
-- Engine: PostgreSQL 12+
-- ERD Alignment: Inventory, Commissions, Holding Fees, System Audit Logs
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1. DIRECT RETAIL NEGOTIATION FLOOR VALIDATION
-- Rule: Agreed Price >= Seller Minimum + Agent Fee (TZS 500) + Platform Fee (MAX(500, 5%))
-- -----------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_validate_negotiation_price(
    p_offered_price DECIMAL(15,2),
    p_seller_minimum DECIMAL(15,2)
) RETURNS BOOLEAN AS $$
DECLARE
    v_agent_fee DECIMAL(15,2) := 500.00;
    v_platform_fee DECIMAL(15,2);
    v_minimum_allowed DECIMAL(15,2);
BEGIN
    -- Calculate Platform Fee: MAX(TZS 500, 5% of offered price)
    v_platform_fee := GREATEST(500.00, p_offered_price * 0.05);
    
    -- Calculate floor price
    v_minimum_allowed := p_seller_minimum + v_agent_fee + v_platform_fee;
    
    IF p_offered_price < v_minimum_allowed THEN
        RETURN FALSE;
    END IF;
    
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;


-- -----------------------------------------------------------------------------
-- 2. WHOLESALE MARGIN & COMMISSION ALLOCATION TRIGGER
-- Rule: Splits 10% target wholesale commercial margin into 35% Investor, 25% Agent, 40% Platform
-- -----------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_allocate_wholesale_margin()
RETURNS TRIGGER AS $$
DECLARE
    v_listing_rec RECORD;
    v_margin DECIMAL(15,2);
    v_investor_share DECIMAL(15,2);
    v_agent_share DECIMAL(15,2);
    v_platform_share DECIMAL(15,2);
BEGIN
    -- Execute only when a wholesale transaction status transitions to 'completed'
    IF (NEW.status = 'completed' AND (OLD.status IS NULL OR OLD.status != 'completed')) THEN
        IF NEW.transaction_model = 'wholesale' THEN
            -- Fetch approved retail price and wholesale acquisition price
            SELECT approved_retail_price, wholesale_price 
            INTO v_listing_rec 
            FROM listings 
            WHERE listing_id = NEW.listing_id;
            
            IF FOUND AND v_listing_rec.wholesale_price IS NOT NULL THEN
                -- Calculate Gross Commercial Margin
                v_margin := v_listing_rec.approved_retail_price - v_listing_rec.wholesale_price;
                
                IF v_margin > 0 THEN
                    v_investor_share := ROUND(v_margin * 0.35, 2);
                    v_agent_share    := ROUND(v_margin * 0.25, 2);
                    v_platform_share := ROUND(v_margin * 0.40, 2);
                    
                    -- Record Commission Breakdown
                    INSERT INTO commission_records (
                        transaction_id,
                        agent_id,
                        investor_id,
                        chuo_sokoni_amount,
                        agent_amount,
                        investor_amount,
                        type
                    ) VALUES (
                        NEW.transaction_id,
                        NEW.agent_id,
                        (SELECT investor_id FROM investors WHERE user_id = NEW.buyer_id LIMIT 1),
                        v_platform_share,
                        v_agent_share,
                        v_investor_share,
                        'wholesale_margin'
                    );
                    
                    -- Update Agent Accumulated Earnings
                    IF NEW.agent_id IS NOT NULL THEN
                        UPDATE agents 
                        SET total_commission = total_commission + v_agent_share 
                        WHERE agent_id = NEW.agent_id;
                    END IF;
                END IF;
            END IF;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_allocate_wholesale_margin
AFTER UPDATE ON transactions
FOR EACH ROW
EXECUTE FUNCTION fn_allocate_wholesale_margin();


-- -----------------------------------------------------------------------------
-- 3. AUTOMATED HOLDING FEE ESCALATION PROCEDURE
-- Rule: Applies escalating holding fee tiers for Direct Retail listings unsold after 14 days
-- -----------------------------------------------------------------------------

CREATE OR REPLACE PROCEDURE sp_apply_listing_holding_fees()
LANGUAGE plpgsql
AS $$
DECLARE
    r_listing RECORD;
    v_days_active INT;
    v_fee_amount DECIMAL(15,2);
BEGIN
    FOR r_listing IN 
        SELECT listing_id, created_at 
        FROM listings 
        WHERE transaction_model = 'direct_retail' 
          AND status = 'active'
    LOOP
        v_days_active := EXTRACT(DAY FROM (CURRENT_TIMESTAMP - r_listing.created_at));
        
        -- Check if listing exceeds the initial 14-day grace period
        IF v_days_active > 14 THEN
            -- Calculate escalating fee tier (e.g., TZS 1,000 per additional 14-day block)
            v_fee_amount := FLOOR((v_days_active - 1) / 14) * 1000.00;
            
            -- Insert or update pending holding fee record
            IF NOT EXISTS (
                SELECT 1 FROM holding_fees 
                WHERE listing_id = r_listing.listing_id 
                  AND due_date = CURRENT_DATE
            ) THEN
                INSERT INTO holding_fees (
                    listing_id,
                    fee_amount,
                    due_date,
                    status
                ) VALUES (
                    r_listing.listing_id,
                    v_fee_amount,
                    CURRENT_DATE,
                    'pending'
                );
            END IF;
        END IF;
    END LOOP;
END;
$$;

-- -----------------------------------------------------------------------------
-- 4. INVENTORY STOCK DEDUCTION & MOVEMENT LOGGING
-- Updates inventory_stock quantity and creates stock_movements entry on completed orders
-- -----------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_process_order_stock_deduction()
RETURNS TRIGGER AS $$
DECLARE
    r_item RECORD;
BEGIN
    -- Deduct stock when order changes status to 'processing' or 'shipped'
    IF (NEW.order_status IN ('processing', 'shipped') AND OLD.order_status = 'pending') THEN

        FOR r_item IN
            SELECT product_id, quantity
            FROM order_items
            WHERE order_id = NEW.order_id
        LOOP

            -- Deduct stock quantity
            UPDATE inventory_stock
            SET quantity = quantity - r_item.quantity,
                last_updated = CURRENT_TIMESTAMP
            WHERE product_id = r_item.product_id;

            -- Update overall product table stock
            UPDATE products
            SET stock_quantity = stock_quantity - r_item.quantity,
                updated_at = CURRENT_TIMESTAMP
            WHERE product_id = r_item.product_id;

            -- Record Stock Movement Audit
            INSERT INTO stock_movements (
                product_id,
                type,
                quantity,
                reference_id
            ) VALUES (
                r_item.product_id,
                'outflow',
                r_item.quantity,
                NEW.order_id
            );

        END LOOP;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_process_order_stock_deduction
AFTER UPDATE ON orders
FOR EACH ROW
EXECUTE FUNCTION fn_process_order_stock_deduction();

-- -----------------------------------------------------------------------------
-- 5. AUDIT LOGGING FOR USER REGISTRATION & SECURITY EVENTS
-- Writes key user lifecycle events to system_logs table
-- -----------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_log_user_activity()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO system_logs (user_id, action, module, details)
        VALUES (NEW.user_id, 'USER_REGISTERED', 'User & Access', 'New account created with status: ' || NEW.status);
    ELSIF (TG_OP = 'UPDATE') THEN
        IF (OLD.status != NEW.status) THEN
            INSERT INTO system_logs (user_id, action, module, details)
            VALUES (NEW.user_id, 'STATUS_CHANGED', 'User & Access', 'Status updated from ' || OLD.status || ' to ' || NEW.status);
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_log_user_activity
AFTER INSERT OR UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION fn_log_user_activity();

COMMIT;
