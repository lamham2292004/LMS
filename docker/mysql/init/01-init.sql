-- ==============================================================================
-- Initial Database Setup for LMS
-- This script runs automatically when MySQL container starts
-- ==============================================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS LMS 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE LMS;

-- Grant privileges (if using non-root user)
-- GRANT ALL PRIVILEGES ON LMS.* TO 'lms_user'@'%';
-- FLUSH PRIVILEGES;

-- Add any initial schema or data here
-- Example:
-- CREATE TABLE IF NOT EXISTS app_settings (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     setting_key VARCHAR(255) NOT NULL UNIQUE,
--     setting_value TEXT,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );
