-- Smart Expense Tracker with AI Insights
-- Database setup script (optional — Hibernate ddl-auto=update will also create these automatically)

CREATE DATABASE IF NOT EXISTS expense_tracker_db;
USE expense_tracker_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    expense_date DATE NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS incomes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    source VARCHAR(100) NOT NULL,
    income_date DATE NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_income_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    monthly_limit DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_budget_user_month_year UNIQUE (user_id, month, year)
);
