package com.portfolio.repository;

import com.portfolio.entity.StockCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for StockCategory entity.
 * Provides CRUD operations for stock categories.
 */
@Repository
public interface StockCategoryRepository extends JpaRepository<StockCategory, Long> {

    /**
     * Find category by name.
     */
    Optional<StockCategory> findByCategoryName(String categoryName);

    /**
     * Check if category exists by name.
     */
    boolean existsByCategoryName(String categoryName);
}
