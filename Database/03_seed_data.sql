-- =============================================================================
-- File: 03_seed_data.sql
-- Description: Core System Seed Data & Demo Workflows
-- Engine: PostgreSQL 12+
-- ERD Alignment: Populates System Roles, Default Referral Codes, Seed Users,
--                Universities, Categories, and Baseline System Settings
-- =============================================================================

BEGIN;

-- ----------------------------------------------------------------------------- 
-- 1. SYSTEM ROLES SEED
-- -----------------------------------------------------------------------------
INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'ROLE_ADMIN', 'Platform System Administrator with full administrative access'),
(2, 'ROLE_SELLER', 'Student entrepreneur who owns, produces, or supplies products'),
(3, 'ROLE_INVESTOR', 'Participant who acquires stock under wholesale model for returns'),
(4, 'ROLE_AGENT', 'Sales & Growth Agent recruiting users and promoting products'),
(5, 'ROLE_CUSTOMER', 'Final customer/buyer purchasing products on Chuo Sokoni');

SELECT setval('roles_role_id_seq', (SELECT MAX(role_id) FROM roles));


-- ----------------------------------------------------------------------------- 
-- 2. UNIVERSITIES SEED (Pilot: Mzumbe University)
-- -----------------------------------------------------------------------------
INSERT INTO universities (university_id, name, location, contact_email, contact_phone, status) VALUES
(1, 'Mzumbe University', 'Morogoro, Tanzania', 'info@mzumbe.ac.tz', '+255232604380', 'active'),
(2, 'University of Dar es Salaam', 'Dar es Salaam, Tanzania', 'vc@udsm.ac.tz', '+255222410500', 'active'),
(3, 'Ardhi University', 'Dar es Salaam, Tanzania', 'aru@aru.ac.tz', '+255222771272', 'active');

SELECT setval('universities_university_id_seq', (SELECT MAX(university_id) FROM universities));


-- ----------------------------------------------------------------------------- 
-- 3. CORE SYSTEM USERS & SEED ACCOUNTS
-- Passwords are bcrypt hashes using cost factor 12.
-- Original plaintext seed passwords:
--   Admin    -> bcrypt hash below
--   Agent    -> bcrypt hash below
--   Seller   -> bcrypt hash below
--   Investor -> bcrypt hash below
--   Customer -> bcrypt hash below
-- -----------------------------------------------------------------------------

-- 3.1 Administrator Account
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, status) VALUES
(1, 1, 1, 'Platform Admin', 'admin@chuosokoni.ac.tz', '+255700000001',
 '$2y$12$Xtnv5ItHb/MRDGCg5BNCse2qO8.6Q9ff7bWGa./9N2r0mBXybINpS',
 'active');

-- 3.2 House Agent Account & Default Referral Code (Chuo Sokoni Official)
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, status) VALUES
(2, 4, 1, 'Chuo Sokoni House Agent', 'houseagent@chuosokoni.ac.tz', '+255700000002',
 '$2y$12$k95W9Gd8uJRpan2Y8jsZweA0WO25/wGTukGaKzuCy.Er6XjY/PRqW',
 'active');

INSERT INTO agents (agent_id, user_id, agent_level, total_referrals, total_commission, status) VALUES
(1, 2, 'house', 0, 0.00, 'active');

INSERT INTO referral_codes (referral_code_id, agent_id, code, is_default) VALUES
(1, 1, 'CHUO_DEFAULT', TRUE);

-- 3.3 Sales & Growth Agent (Student Agent)
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, referral_code_used, status) VALUES
(3, 4, 1, 'Juma Agent', 'juma.agent@mzumbe.ac.tz', '+255712345678',
 '$2y$12$k95W9Gd8uJRpan2Y8jsZweA0WO25/wGTukGaKzuCy.Er6XjY/PRqW',
 'CHUO_DEFAULT', 'active');

INSERT INTO agents (agent_id, user_id, agent_level, total_referrals, total_commission, status) VALUES
(2, 3, 'standard', 0, 0.00, 'active');

INSERT INTO referral_codes (referral_code_id, agent_id, code, is_default) VALUES
(2, 2, 'JUMA2026', FALSE);

-- Update Juma as referred by House Agent
UPDATE users SET referred_by = 1 WHERE user_id = 3;

-- 3.4 Seller Account (Student Seller)
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, referral_code_used, referred_by, status) VALUES
(4, 2, 1, 'Amina Student Seller', 'amina.seller@mzumbe.ac.tz', '+255722345678',
 '$2y$12$hs19Vr4kLszxImVFTeoqReOHeYx5zxAwumwy7zvHnZYdlzSc499Oq',
 'JUMA2026', 2, 'active');

INSERT INTO sellers (seller_id, user_id, business_name, business_type, description, verification_status, rating) VALUES
(1, 4, 'Amina Crafts & Stationery', 'Sole Proprietorship',
 'Handmade stationery and student craft supplies', 'approved', 4.80);

-- 3.5 Investor Account
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, referral_code_used, referred_by, status) VALUES
(5, 3, 1, 'Baraka Investor', 'baraka.investor@mzumbe.ac.tz', '+255732345678',
 '$2y$12$nc8iGUxoT8kE2icL/7RXteHtlMxe9ec1YJPMCEtO9EqtNxgWVcxTS',
 'JUMA2026', 2, 'active');

INSERT INTO investors (investor_id, user_id, investment_type, available_capital, risk_preference, status) VALUES
(1, 5, 'Wholesale Stock Financer', 500000.00, 'Moderate', 'active');

-- 3.6 Customer Account
INSERT INTO users (user_id, role_id, university_id, full_name, email, phone, password_hash, referral_code_used, referred_by, status) VALUES
(6, 5, 1, 'Neema Customer', 'neema.customer@mzumbe.ac.tz', '+255742345678',
 '$2y$12$DJxUgT9ne96p6rvDbW82OueHOemL9H6nZPMzqSgxaTY757qcUN4S2',
 'JUMA2026', 2, 'active');

INSERT INTO customers (customer_id, user_id, customer_type, location) VALUES
(1, 6, 'student', 'Mzumbe Main Campus Hostel Block B');

SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
SELECT setval('agents_agent_id_seq', (SELECT MAX(agent_id) FROM agents));
SELECT setval('sellers_seller_id_seq', (SELECT MAX(seller_id) FROM sellers));
SELECT setval('investors_investor_id_seq', (SELECT MAX(investor_id) FROM investors));
SELECT setval('customers_customer_id_seq', (SELECT MAX(customer_id) FROM customers));
SELECT setval('referral_codes_referral_code_id_seq', (SELECT MAX(referral_code_id) FROM referral_codes));


-- ----------------------------------------------------------------------------- 
-- 4. CATEGORIES SEED
-- -----------------------------------------------------------------------------
INSERT INTO categories (category_id, parent_category_id, name, description, status) VALUES
(1, NULL, 'Academic & Stationery', 'Books, notebooks, pens, and study tools', 'active'),
(2, NULL, 'Electronics & Gadgets', 'Phones, accessories, memory devices, chargers', 'active'),
(3, NULL, 'Fashion & Apparel', 'Clothing, shoes, bags, and student fashion', 'active'),
(4, 1, 'Notebooks & Printing', 'Printing services and custom notebooks', 'active'),
(5, 2, 'Mobile Accessories', 'Earphones, power banks, and phone covers', 'active');

SELECT setval('categories_category_id_seq', (SELECT MAX(category_id) FROM categories));


-- ----------------------------------------------------------------------------- 
-- 5. PRODUCTS & CATALOG SEED
-- -----------------------------------------------------------------------------
-- Product 1: Wholesale Eligible Item
INSERT INTO products (product_id, seller_id, category_id, name, description, unit, stock_quantity, status) VALUES
(1, 1, 4, 'Mzumbe Branded Executive Notebook',
 'Hardcover 200-page notebook with university logo', 'piece', 100, 'approved');

INSERT INTO product_images (image_id, product_id, image_url, is_primary) VALUES
(1, 1, 'https://cdn.chuosokoni.ac.tz/images/products/notebook_executive_1.jpg', TRUE);

INSERT INTO inventory_stock (stock_id, product_id, quantity, stock_location) VALUES
(1, 1, 100, 'Mzumbe Central Hub');

-- Product 2: Direct Retail Eligible Item
INSERT INTO products (product_id, seller_id, category_id, name, description, unit, stock_quantity, status) VALUES
(2, 1, 5, 'Fast Charging Power Bank 10000mAh',
 'Dual USB portable fast charger for mobile phones', 'piece', 15, 'approved');

INSERT INTO product_images (image_id, product_id, image_url, is_primary) VALUES
(2, 2, 'https://cdn.chuosokoni.ac.tz/images/products/powerbank_10k_1.jpg', TRUE);

INSERT INTO inventory_stock (stock_id, product_id, quantity, stock_location) VALUES
(2, 2, 15, 'Seller Premises');

SELECT setval('products_product_id_seq', (SELECT MAX(product_id) FROM products));
SELECT setval('product_images_image_id_seq', (SELECT MAX(image_id) FROM product_images));
SELECT setval('inventory_stock_stock_id_seq', (SELECT MAX(stock_id) FROM inventory_stock));


-- ----------------------------------------------------------------------------- 
-- 6. PRODUCT LISTINGS SEED
-- -----------------------------------------------------------------------------
-- Listing 1: Wholesale Model (Retail Price = TZS 20,000 | Wholesale = TZS 18,000 [90% target])
INSERT INTO listings (listing_id, product_id, seller_id, transaction_model, listing_type, approved_retail_price, wholesale_price, negotiation_enabled, status) VALUES
(1, 1, 1, 'wholesale', 'wholesale', 20000.00, 18000.00, FALSE, 'active');

-- Listing 2: Direct Retail Model (Retail Price = TZS 35,000 | Direct Retail with Negotiation)
INSERT INTO listings (listing_id, product_id, seller_id, transaction_model, listing_type, approved_retail_price, wholesale_price, negotiation_enabled, status) VALUES
(2, 2, 1, 'direct_retail', 'direct_retail', 35000.00, NULL, TRUE, 'active');

SELECT setval('listings_listing_id_seq', (SELECT MAX(listing_id) FROM listings));


-- ----------------------------------------------------------------------------- 
-- 7. CUSTOMER ADDRESS SEED
-- -----------------------------------------------------------------------------
INSERT INTO addresses (address_id, user_id, address_type, full_name, phone, street, city, region, country, is_default) VALUES
(1, 6, 'shipping', 'Neema Customer', '+255742345678',
 'Hostel Block B, Room 104', 'Morogoro', 'Morogoro Region', 'Tanzania', TRUE);

SELECT setval('addresses_address_id_seq', (SELECT MAX(address_id) FROM addresses));


COMMIT;