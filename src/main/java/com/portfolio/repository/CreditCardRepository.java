package com.portfolio.repository;

import com.portfolio.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for CreditCard entity.
 * Provides CRUD operations and queries for credit card management.
 */
@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    /**
     * Find all credit cards belonging to a specific portfolio.
     */
    List<CreditCard> findByPortfolioPortfolioId(Long portfolioId);

    /**
     * Find credit cards with due dates before or on a specific date.
     */
    @Query("SELECT c FROM CreditCard c WHERE c.portfolio.portfolioId = :portfolioId " +
           "AND c.dueDate <= :date ORDER BY c.dueDate")
    List<CreditCard> findUpcomingDueCards(@Param("portfolioId") Long portfolioId, 
                                           @Param("date") LocalDate date);

    /**
     * Find overdue credit cards.
     */
    @Query("SELECT c FROM CreditCard c WHERE c.portfolio.portfolioId = :portfolioId " +
           "AND c.dueDate < :today ORDER BY c.dueDate")
    List<CreditCard> findOverdueCards(@Param("portfolioId") Long portfolioId, 
                                       @Param("today") LocalDate today);
}
