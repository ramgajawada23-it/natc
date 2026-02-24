-- ============================================
-- Database Schema for Department-Employee System
-- ============================================

-- Create Database (Optional - Commented out for managed environments like Aiven)
-- CREATE DATABASE IF NOT EXISTS employee_db
-- CHARACTER SET utf8mb4
-- COLLATE utf8mb4_unicode_ci;

-- USE employee_db;

-- ============================================
-- Department Table
-- ============================================
CREATE TABLE IF NOT EXISTS department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_department_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Employee Table
-- ============================================
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES department(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    INDEX idx_employee_name (name),
    INDEX idx_employee_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Sample Data (Optional)
-- ============================================
INSERT INTO department (name) VALUES 
    ('Human Resources'),
    ('Information Technology'),
    ('Finance'),
    ('Marketing')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO employee (name, department_id) VALUES 
    ('John Doe', 1),
    ('Jane Smith', 2),
    ('Mike Johnson', 2),
    ('Sarah Williams', 3)
ON DUPLICATE KEY UPDATE name=name;
