
-- =============================================================================
-- File: 02_indexes_constraints.sql
-- Description: Performance Indexes, Foreign Key Polish, and Business Rule Constraints
-- Engine: PostgreSQL 12+
-- ERD Alignment: Across all 6 Normalized Modules
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1. FOREIGN KEY INDEXES (Optimizes JOIN performance across relational trees)
-- -----------------------------------------------------------------------------

-- User & Access Management Module
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_university_id ON users(university_id);
CREATE INDEX idx_users_referred_by ON users(referred_by);
CREATE INDEX idx_sellers_user_id ON sellers(user_id);
CREATE INDEX idx_investors_user_id ON investors(user_id);
CREATE INDEX idx_agents_user_id ON agents(user_id);
CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_referral_codes_agent_id ON referral_codes(agent_id);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);

-- Product & Catalog Management Module
CREATE INDEX idx_categories_parent_id ON categories(parent_category_id);
CREATE INDEX idx_products_seller_id ON products(seller_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_listings_product_id ON listings(product_id);
CREATE INDEX idx_listings_seller_id ON listings(seller_id);
CREATE INDEX idx_inventory_stock_product_id ON inventory_stock(product_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_holding_fees_listing_id ON holding_fees(listing_id);

-- Transactions & Orders Module
CREATE INDEX idx_transactions_listing_id ON transactions(listing_id);
CREATE INDEX idx_transactions_buyer_id ON transactions(buyer_id);
CREATE INDEX idx_transactions_seller_id ON transactions(seller_id);
CREATE INDEX idx_transactions_agent_id ON transactions(agent_id);
CREATE INDEX idx_orders_transaction_id ON orders(transaction_id);
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_shipping_address_id ON orders(shipping_address_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Payments & Refunds Module
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payment_transactions_payment_id ON payment_transactions(payment_id);
CREATE INDEX idx_refunds_payment_txn_id ON refunds(payment_txn_id);
CREATE INDEX idx_refunds_order_id ON refunds(order_id);

-- Commissions & Returns Module
CREATE INDEX idx_commission_records_transaction_id ON commission_records(transaction_id);
CREATE INDEX idx_commission_records_agent_id ON commission_records(agent_id);
CREATE INDEX idx_commission_records_investor_id ON commission_records(investor_id);
CREATE INDEX idx_commission_reversals_commission_id ON commission_reversals(commission_id);

-- Support, Notifications & Logs Module
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_disputes_order_id ON disputes(order_id);
CREATE INDEX idx_disputes_initiated_by ON disputes(initiated_by);
CREATE INDEX idx_withdrawals_user_id ON withdrawals(user_id);
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);


-- -----------------------------------------------------------------------------
-- 2. QUERY OPTIMIZATION INDEXES (Filtering, Searching, & Cron Jobs)
-- -----------------------------------------------------------------------------

-- Fast lookup for user login and auth
CREATE INDEX idx_users_email_lower ON users(LOWER(email));
CREATE INDEX idx_users_phone ON users(phone);

-- Search and filtering on product catalog
CREATE INDEX idx_products_status_created ON products(status, created_at DESC);
CREATE INDEX idx_products_name_trgm ON products USING btree(name);
CREATE INDEX idx_listings_model_status ON listings(transaction_model, status);

-- Order, Payment, and Commission tracking
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_payments_status_ref ON payments(status, reference_number);
CREATE INDEX idx_commission_records_type ON commission_records(type);

-- Holding Fee tracking (Unsold Direct Retail listings past 14 days)
CREATE INDEX idx_holding_fees_status_due ON holding_fees(status, due_date);

-- Unread Notifications lookup
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;


-- -----------------------------------------------------------------------------
-- 3. BUSINESS RULE CHECK CONSTRAINTS
-- -----------------------------------------------------------------------------

-- Numerical non-negativity checks
ALTER TABLE products 
    ADD CONSTRAINT chk_products_stock_quantity CHECK (stock_quantity >= 0);

ALTER TABLE listings 
    ADD CONSTRAINT chk_listings_approved_price CHECK (approved_retail_price >= 0.00),
    ADD CONSTRAINT chk_listings_wholesale_price CHECK (wholesale_price IS NULL OR wholesale_price >= 0.00);

ALTER TABLE inventory_stock 
    ADD CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0);

ALTER TABLE transactions 
    ADD CONSTRAINT chk_transactions_amounts CHECK (
        total_amount >= 0.00 AND 
        platform_fee >= 0.00 AND 
        agent_commission >= 0.00 AND 
        investor_return >= 0.00
    );

ALTER TABLE order_items 
    ADD CONSTRAINT chk_order_items_qty_price CHECK (quantity > 0 AND unit_price >= 0.00 AND subtotal >= 0.00);

ALTER TABLE holding_fees 
    ADD CONSTRAINT chk_holding_fees_amount CHECK (fee_amount >= 0.00);

ALTER TABLE withdrawals 
    ADD CONSTRAINT chk_withdrawals_amount CHECK (amount > 0.00);


-- -----------------------------------------------------------------------------
-- 4. UNIQUE CONDITIONAL CONSTRAINTS
-- -----------------------------------------------------------------------------

-- Ensure only ONE primary image exists per product
CREATE UNIQUE INDEX uq_primary_product_image 
    ON product_images (product_id) 
    WHERE is_primary = TRUE;

-- Ensure only ONE default address exists per user
CREATE UNIQUE INDEX uq_default_user_address 
    ON addresses (user_id) 
    WHERE is_default = TRUE;

-- Ensure only ONE active listing per product
CREATE UNIQUE INDEX uq_active_product_listing 
    ON listings (product_id) 
    WHERE status = 'active';

COMMIT;