package com.portfolio.repository;

import com.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Portfolio entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Find portfolio by name.
     */
    Optional<Portfolio> findByPortfolioName(String portfolioName);

    /**
     * Check if portfolio exists by name.
     */
    boolean existsByPortfolioName(String portfolioName);
}
