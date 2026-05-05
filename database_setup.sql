-- ===================================
-- REVIEWS TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(100),
    comment TEXT,
    verified_purchase BOOLEAN DEFAULT TRUE,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_review (user_id, product_id, order_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
-- ===================================
-- FOOD EXPRESS DATABASE SETUP
-- ===================================

-- Create Database
CREATE DATABASE IF NOT EXISTS food_express_db;
USE food_express_db;

-- ===================================
-- USERS TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===================================
-- USER_ADDRESSES TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    label VARCHAR(40),
    address_line VARCHAR(500) NOT NULL,
    city VARCHAR(120),
    state VARCHAR(120),
    pincode VARCHAR(20),
    landmark VARCHAR(200),
    latitude DOUBLE,
    longitude DOUBLE,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ===================================
-- PRODUCTS TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    ingredients TEXT,
    price DECIMAL(8, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    category VARCHAR(50),
    image_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===================================
-- ORDERS TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10, 2),
    gst DECIMAL(10, 2),
    delivery_charge DECIMAL(10, 2),
    total_amount DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ===================================
-- PAYMENTS TABLE (RAZORPAY)
-- ===================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    razorpay_order_id VARCHAR(255) UNIQUE,
    razorpay_payment_id VARCHAR(255) UNIQUE,
    razorpay_signature VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
-- ===================================
-- ORDER_ITEMS TABLE
-- ===================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(8, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- ===================================
-- SAMPLE ADMIN USER
-- ===================================
-- Email: admin@foodexpress.com
-- Password: admin123 (encoded with BCrypt)
INSERT INTO users (full_name, email, password, mobile_number, role, enabled) VALUES
('Admin User', 'admin@foodexpress.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/lvO', '9999999999', 'ROLE_ADMIN', TRUE);

-- ===================================
-- SAMPLE PRODUCTS
-- ===================================
INSERT INTO products (name, description, ingredients, price, stock_quantity, category, image_name) VALUES
('Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 'Pizza base, tomato sauce, mozzarella, basil', 250.00, 25, 'Pizza', 'pizza1.jpg'),
('Pepperoni Pizza', 'Delicious pizza with pepperoni and cheese', 'Pizza base, tomato sauce, mozzarella, pepperoni', 300.00, 18, 'Pizza', 'pizza2.jpg'),
('Chicken Burger', 'Juicy chicken burger with special sauce', 'Burger bun, chicken patty, lettuce, mayo, sauce', 180.00, 12, 'Burger', 'burger1.jpg'),
('Veggie Burger', 'Fresh vegetable burger with hummus', 'Burger bun, veggie patty, lettuce, hummus, tomato', 150.00, 9, 'Burger', 'burger2.jpg'),
('Biryani', 'Fragrant rice with tender meat', 'Basmati rice, spices, meat, herbs, fried onions', 280.00, 8, 'Rice', 'biryani1.jpg'),
('Chole Bhature', 'Fluffy bhature with spicy chole curry', 'Wheat flour, chole, spices, oil, yogurt', 120.00, 6, 'Indian', 'chole1.jpg'),
('Coke 200ml', 'Cold refreshing cola', 'Carbonated water, sugar, caffeine, flavoring', 40.00, 40, 'Drinks', 'coke.jpg'),
('Lassi', 'Traditional yogurt drink', 'Yogurt, water, sugar, salt, cardamom', 60.00, 5, 'Drinks', 'lassi.jpg');

-- ===================================
-- VERIFY TABLES
-- ===================================
SELECT 'Database setup completed successfully!' AS Status;
SHOW TABLES;
