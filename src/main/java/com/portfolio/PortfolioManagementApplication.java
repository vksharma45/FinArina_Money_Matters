package com.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for Portfolio Management System.
 * 
 * This system provides backend APIs for managing investment portfolios
 * including stocks, mutual funds, bonds, ETFs, cash, and credit cards.
 * 
 * Key Features:
 * - Portfolio creation and management
 * - Asset tracking with performance calculations
 * - Stock categorization with category-level performance
 * - Credit card tracking with due date alerts
 * - Portfolio value and returns calculation
 * - Asset allocation analysis
 */
@SpringBootApplication
public class PortfolioManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioManagementApplication.class, args);
    }
}
