
-- =============================================================================
-- File: 01_schema_tables.sql
-- Description: Complete Database Schema for Chuo Sokoni Application
-- Engine: PostgreSQL 12+
-- ERD Alignment: User & Access, Product & Catalog, Transactions & Orders,
--                Payments & Refunds, Commissions & Returns, Support & Logs
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 0. CUSTOM ENUM TYPES
-- -----------------------------------------------------------------------------

CREATE TYPE user_status_enum AS ENUM (
    'pending', 
    'verify', 
    'active', 
    'suspended', 
    'rejected'
);

CREATE TYPE verification_status_enum AS ENUM (
    'pending', 
    'approved', 
    'rejected'
);

CREATE TYPE category_status_enum AS ENUM (
    'active', 
    'inactive'
);

CREATE TYPE product_status_enum AS ENUM (
    'draft', 
    'pending_approval', 
    'approved', 
    'rejected', 
    'archived'
);

CREATE TYPE transaction_model_enum AS ENUM (
    'wholesale', 
    'direct_retail'
);

CREATE TYPE listing_type_enum AS ENUM (
    'wholesale', 
    'direct_retail'
);

CREATE TYPE listing_status_enum AS ENUM (
    'active', 
    'paused', 
    'sold_out', 
    'expired', 
    'removed'
);

CREATE TYPE stock_movement_type_enum AS ENUM (
    'inflow', 
    'outflow', 
    'adjustment', 
    'hold', 
    'release'
);

CREATE TYPE holding_fee_status_enum AS ENUM (
    'pending', 
    'applied', 
    'waived', 
    'cleared'
);

CREATE TYPE transaction_status_enum AS ENUM (
    'pending', 
    'in_progress', 
    'completed', 
    'failed', 
    'cancelled', 
    'reversed'
);

CREATE TYPE order_status_enum AS ENUM (
    'pending', 
    'processing', 
    'shipped', 
    'delivered', 
    'confirmed', 
    'disputed', 
    'cancelled'
);

CREATE TYPE payment_status_enum AS ENUM (
    'pending', 
    'successful', 
    'failed', 
    'reversed'
);

CREATE TYPE refund_status_enum AS ENUM (
    'pending', 
    'approved', 
    'processed', 
    'rejected'
);

CREATE TYPE commission_status_enum AS ENUM (
    'pending', 
    'earned', 
    'paid', 
    'reversed'
);

CREATE TYPE commission_type_enum AS ENUM (
    'wholesale_margin', 
    'direct_retail_fee'
);

CREATE TYPE withdrawal_status_enum AS ENUM (
    'requested', 
    'processing', 
    'completed', 
    'rejected'
);

CREATE TYPE notification_type_enum AS ENUM (
    'order_update', 
    'payment_alert', 
    'negotiation_offer', 
    'holding_fee_warning', 
    'approval_update', 
    'system_alert'
);

CREATE TYPE dispute_type_enum AS ENUM (
    'item_not_received', 
    'item_defective', 
    'incorrect_item', 
    'payment_issue', 
    'other'
);

CREATE TYPE dispute_status_enum AS ENUM (
    'open', 
    'under_review', 
    'resolved', 
    'escalated', 
    'closed'
);

CREATE TYPE address_type_enum AS ENUM (
    'shipping', 
    'billing', 
    'business'
);


-- -----------------------------------------------------------------------------
-- 1. USER & ACCESS MANAGEMENT MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE universities (
    university_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    status category_status_enum DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    role_id INT NOT NULL REFERENCES roles(role_id) ON DELETE RESTRICT,
    university_id INT REFERENCES universities(university_id) ON DELETE SET NULL,
    referred_by INT, -- Self-referencing FK added after referral_codes table
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    referral_code_used VARCHAR(20),
    status user_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sellers (
    seller_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    business_name VARCHAR(100) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    description TEXT,
    verification_status verification_status_enum DEFAULT 'pending',
    rating DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE investors (
    investor_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    investment_type VARCHAR(50),
    available_capital DECIMAL(15,2) DEFAULT 0.00,
    risk_preference VARCHAR(50),
    status user_status_enum DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agents (
    agent_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    agent_level VARCHAR(20) DEFAULT 'standard',
    total_referrals INT DEFAULT 0,
    total_commission DECIMAL(15,2) DEFAULT 0.00,
    status user_status_enum DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    customer_type VARCHAR(50) DEFAULT 'student',
    location VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referral_codes (
    referral_code_id SERIAL PRIMARY KEY,
    agent_id INT NOT NULL REFERENCES agents(agent_id) ON DELETE CASCADE,
    code VARCHAR(20) NOT NULL UNIQUE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add Foreign Key constraint for referred_by in users table
ALTER TABLE users 
    ADD CONSTRAINT fk_users_referred_by 
    FOREIGN KEY (referred_by) REFERENCES agents(agent_id) ON DELETE SET NULL;

CREATE TABLE addresses (
    address_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    address_type address_type_enum DEFAULT 'shipping',
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    street VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    country VARCHAR(50) DEFAULT 'Tanzania',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- -----------------------------------------------------------------------------
-- 2. PRODUCT & CATALOG MANAGEMENT MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    parent_category_id INT REFERENCES categories(category_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status category_status_enum DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL REFERENCES sellers(seller_id) ON DELETE CASCADE,
    category_id INT NOT NULL REFERENCES categories(category_id) ON DELETE RESTRICT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    unit VARCHAR(20) DEFAULT 'item',
    stock_quantity INT NOT NULL DEFAULT 0,
    status product_status_enum DEFAULT 'draft',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_images (
    image_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
    image_url VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE listings (
    listing_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
    seller_id INT NOT NULL REFERENCES sellers(seller_id) ON DELETE CASCADE,
    transaction_model transaction_model_enum NOT NULL,
    listing_type listing_type_enum NOT NULL,
    approved_retail_price DECIMAL(15,2) NOT NULL,
    wholesale_price DECIMAL(15,2),
    negotiation_enabled BOOLEAN DEFAULT FALSE,
    status listing_status_enum DEFAULT 'active',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_stock (
    stock_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 0,
    stock_location VARCHAR(100) DEFAULT 'Seller Premises',
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stock_movements (
    movement_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
    type stock_movement_type_enum NOT NULL,
    quantity INT NOT NULL,
    reference_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE holding_fees (
    fee_id SERIAL PRIMARY KEY,
    listing_id INT NOT NULL REFERENCES listings(listing_id) ON DELETE CASCADE,
    fee_amount DECIMAL(15,2) NOT NULL,
    due_date DATE NOT NULL,
    status holding_fee_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- -----------------------------------------------------------------------------
-- 3. TRANSACTIONS & ORDERS MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    listing_id INT NOT NULL REFERENCES listings(listing_id) ON DELETE RESTRICT,
    buyer_id INT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    seller_id INT NOT NULL REFERENCES sellers(seller_id) ON DELETE RESTRICT,
    agent_id INT REFERENCES agents(agent_id) ON DELETE SET NULL,
    transaction_model transaction_model_enum NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    platform_fee DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    agent_commission DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    investor_return DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status transaction_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL REFERENCES transactions(transaction_id) ON DELETE RESTRICT,
    buyer_id INT NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    seller_id INT NOT NULL REFERENCES sellers(seller_id) ON DELETE RESTRICT,
    shipping_address_id INT NOT NULL REFERENCES addresses(address_id) ON DELETE RESTRICT,
    order_status order_status_enum DEFAULT 'pending',
    total_amount DECIMAL(15,2) NOT NULL,
    payment_status payment_status_enum DEFAULT 'pending',
    tracking_number VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(product_id) ON DELETE RESTRICT,
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL
);


-- -----------------------------------------------------------------------------
-- 4. PAYMENTS & REFUNDS MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE RESTRICT,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    payment_provider VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'TZS',
    payment_method VARCHAR(50) NOT NULL,
    status payment_status_enum DEFAULT 'pending',
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_transactions (
    payment_txn_id SERIAL PRIMARY KEY,
    payment_id INT NOT NULL REFERENCES payments(payment_id) ON DELETE CASCADE,
    provider_response TEXT,
    transaction_id VARCHAR(100),
    status payment_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refunds (
    refund_id SERIAL PRIMARY KEY,
    payment_txn_id INT NOT NULL REFERENCES payment_transactions(payment_txn_id) ON DELETE RESTRICT,
    order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE RESTRICT,
    reason TEXT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status refund_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- -----------------------------------------------------------------------------
-- 5. COMMISSIONS & RETURNS MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE commission_records (
    commission_id SERIAL PRIMARY KEY,
    transaction_id INT NOT NULL REFERENCES transactions(transaction_id) ON DELETE RESTRICT,
    agent_id INT NOT NULL REFERENCES agents(agent_id) ON DELETE RESTRICT,
    investor_id INT REFERENCES investors(investor_id) ON DELETE SET NULL,
    chuo_sokoni_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    agent_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    investor_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type commission_type_enum NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE commission_reversals (
    reversal_id SERIAL PRIMARY KEY,
    commission_id INT NOT NULL REFERENCES commission_records(commission_id) ON DELETE RESTRICT,
    reason TEXT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status commission_status_enum DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- -----------------------------------------------------------------------------
-- 6. SUPPORT, NOTIFICATIONS & LOGS MODULE
-- -----------------------------------------------------------------------------

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    type notification_type_enum NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE disputes (
    dispute_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE RESTRICT,
    initiated_by INT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    reason TEXT NOT NULL,
    resolution TEXT,
    status dispute_status_enum DEFAULT 'open',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE withdrawals (
    withdrawal_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    amount DECIMAL(15,2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status withdrawal_status_enum DEFAULT 'requested',
    reference_number VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    module VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMIT;