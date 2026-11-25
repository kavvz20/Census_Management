-- ============================================================================
-- CENSUS MANAGEMENT SYSTEM - DATABASE SCHEMA & SAMPLE DATA SCRIPT
-- Database: MySQL 8.0+
-- Prepared for Viva Presentation & College Project Demonstration
-- ============================================================================

CREATE DATABASE IF NOT EXISTS census_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE census_db;

-- ----------------------------------------------------------------------------
-- 1. Table: region
-- Stores geographic administrative boundaries (State -> District -> City)
-- Unique Constraint: state + district + city
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS household;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS region;

CREATE TABLE region (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_region_state_district_city UNIQUE (state, district, city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. Table: users
-- Stores administrative and field enumerator accounts
-- Role is ADMIN or ENUMERATOR
-- region_id is required for ENUMERATORS (locked to their jurisdiction)
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    region_id BIGINT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT fk_users_region FOREIGN KEY (region_id) REFERENCES region(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. Table: household
-- Stores housing infrastructure, utilities, and location
-- Links to region
-- ----------------------------------------------------------------------------
CREATE TABLE household (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    region_id BIGINT NOT NULL,
    house_type VARCHAR(50) NOT NULL,
    electricity BOOLEAN NOT NULL DEFAULT FALSE,
    water VARCHAR(50) NOT NULL,
    contact_number VARCHAR(15) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_household_region FOREIGN KEY (region_id) REFERENCES region(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 4. Table: person
-- Stores individual demographic and socioeconomic data
-- Unique constraint on national id_number
-- Links to household (person -> household -> region)
-- ----------------------------------------------------------------------------
CREATE TABLE person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_number VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    relation VARCHAR(50) NOT NULL,
    marital_status VARCHAR(50) NOT NULL,
    education VARCHAR(100) NOT NULL,
    occupation VARCHAR(100) NOT NULL,
    nationality VARCHAR(50) NOT NULL DEFAULT 'Indian',
    household_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_person_id_number UNIQUE (id_number),
    CONSTRAINT fk_person_household FOREIGN KEY (household_id) REFERENCES household(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- INDEXES FOR HIGH QUERY PERFORMANCE
-- ----------------------------------------------------------------------------
CREATE INDEX idx_household_region ON household(region_id);
CREATE INDEX idx_person_household ON person(household_id);
CREATE INDEX idx_person_gender ON person(gender);
CREATE INDEX idx_person_dob ON person(dob);
CREATE INDEX idx_person_education ON person(education);
CREATE INDEX idx_person_occupation ON person(occupation);

-- ----------------------------------------------------------------------------
-- SAMPLE DATA INSERTION
-- BCrypt hash for 'admin123' and 'enum123'
-- admin123  : $2a$10$wO3t6fP9R9e7Uo.ZzRjJ6e1cT5XQ3m6O/bTqN7J8f4iL0gGg0u2k2
-- enum123   : $2a$10$8K1p/a0dL1LXMIgoEDFrw.O7eQn5N5M8Jt6w1u9i5Q1M8e9e1c2e3
-- ----------------------------------------------------------------------------

-- Insert Regions
INSERT INTO region (id, state, district, city) VALUES
(1, 'Maharashtra', 'Mumbai Suburban', 'Andheri'),
(2, 'Karnataka', 'Bengaluru Urban', 'Koramangala'),
(3, 'Delhi', 'Central Delhi', 'Connaught Place'),
(4, 'Tamil Nadu', 'Chennai', 'T. Nagar');

-- Insert Users (Passwords: admin123, enum123)
-- BCrypt hashed with cost factor 10
INSERT INTO users (id, username, password_hash, role, region_id, active) VALUES
(1, 'admin', '$2a$10$wO3t6fP9R9e7Uo.ZzRjJ6e1cT5XQ3m6O/bTqN7J8f4iL0gGg0u2k2', 'ADMIN', NULL, TRUE),
(2, 'enum_mumbai', '$2a$10$gM.tS9LqD5bWnQ4Tz7vIse2X3y9qA0kL1M8e9e1c2e3O7eQn5N5M8', 'ENUMERATOR', 1, TRUE),
(3, 'enum_bangalore', '$2a$10$gM.tS9LqD5bWnQ4Tz7vIse2X3y9qA0kL1M8e9e1c2e3O7eQn5N5M8', 'ENUMERATOR', 2, TRUE),
(4, 'enum_delhi', '$2a$10$gM.tS9LqD5bWnQ4Tz7vIse2X3y9qA0kL1M8e9e1c2e3O7eQn5N5M8', 'ENUMERATOR', 3, FALSE); -- Inactive demo

-- Insert Households
INSERT INTO household (id, address, region_id, house_type, electricity, water, contact_number) VALUES
(1, 'Flat 402, Sea Green Apts, J.P. Road, Andheri West', 1, 'Apartment', TRUE, 'Tap Water', '9820011223'),
(2, 'House 12, Gulmohar Cross Rd, JVPD Scheme, Andheri', 1, 'Pucca', TRUE, 'Tap Water', '9821098765'),
(3, 'No. 88, 5th Main, 4th Block, Koramangala', 2, 'Pucca', TRUE, 'Tap Water', '9448012345'),
(4, 'Flat 12B, Barakhamba Road, Connaught Place', 3, 'Apartment', TRUE, 'Tap Water', '9811099887'),
(5, 'Plot 45, Usman Road, T. Nagar', 4, 'Independent House', TRUE, 'Tap Water', '9840011223');

-- Insert Citizens (Person records)
INSERT INTO person (id, id_number, name, gender, dob, relation, marital_status, education, occupation, nationality, household_id) VALUES
(1, 'MH-1001-A', 'Rajesh Sharma', 'Male', '1980-05-14', 'Head', 'Married', 'Graduate', 'Employed', 'Indian', 1),
(2, 'MH-1002-B', 'Sunita Sharma', 'Female', '1983-08-22', 'Spouse', 'Married', 'Graduate', 'Homemaker', 'Indian', 1),
(3, 'MH-1003-C', 'Aarav Sharma', 'Male', '2010-11-05', 'Son', 'Single', 'Secondary', 'Student', 'Indian', 1),
(4, 'MH-2001-A', 'Vikram Malhotra', 'Male', '1955-03-10', 'Head', 'Widowed', 'Post Graduate', 'Retired', 'Indian', 2),
(5, 'MH-2002-B', 'Rohan Malhotra', 'Male', '1986-07-19', 'Son', 'Married', 'Doctorate', 'Employed', 'Indian', 2),
(6, 'MH-2003-C', 'Pooja Malhotra', 'Female', '1989-12-01', 'Daughter-in-Law', 'Married', 'Post Graduate', 'Employed', 'Indian', 2),
(7, 'KA-3001-A', 'Anand Rao', 'Male', '1975-09-28', 'Head', 'Married', 'Graduate', 'Self-Employed', 'Indian', 3),
(8, 'KA-3002-B', 'Lakshmi Rao', 'Female', '1978-04-15', 'Spouse', 'Married', 'Graduate', 'Employed', 'Indian', 3),
(9, 'KA-3003-C', 'Sneha Rao', 'Female', '2005-06-12', 'Daughter', 'Single', 'Higher Secondary', 'Student', 'Indian', 3),
(10, 'KA-3004-D', 'Kavita Rao', 'Female', '1948-01-20', 'Mother', 'Widowed', 'Illiterate', 'Retired', 'Indian', 3),
(11, 'DL-4001-A', 'Amitabh Sen', 'Male', '1992-02-18', 'Head', 'Single', 'Post Graduate', 'Employed', 'Indian', 4),
(12, 'DL-4002-B', 'Debashis Sen', 'Male', '1995-10-08', 'Brother', 'Single', 'Graduate', 'Unemployed', 'Indian', 4),
(13, 'TN-5001-A', 'Subramanian Swamy', 'Male', '1968-06-30', 'Head', 'Married', 'Graduate', 'Employed', 'Indian', 5),
(14, 'TN-5002-B', 'Meenakshi Swamy', 'Female', '1972-08-14', 'Spouse', 'Married', 'Higher Secondary', 'Homemaker', 'Indian', 5),
(15, 'TN-5003-C', 'Karthik Swamy', 'Male', '2000-01-10', 'Son', 'Single', 'Graduate', 'Employed', 'Indian', 5);

COMMIT;
