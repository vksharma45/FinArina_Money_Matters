package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * CreditCard entity for tracking credit card dues and payments.
 * Includes automatic due date status calculation.
 */
@Entity
@Table(name = "credit_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    @Column(name = "credit_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "outstanding_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal outstandingAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Calculates the due date status based on current date.
     * Returns:
     * - "OVERDUE" if due date has passed
     * - "WARNING" if due date is within next 5 days
     * - "OK" otherwise
     */
    public String getDueStatus() {
        LocalDate today = LocalDate.now();
        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

        if (daysUntilDue < 0) {
            return "OVERDUE";
        } else if (daysUntilDue <= 5) {
            return "WARNING";
        } else {
            return "OK";
        }
    }

    /**
     * Calculates days until due date.
     * Negative value means overdue.
     */
    public long getDaysUntilDue() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Calculates available credit.
     */
    public BigDecimal getAvailableCredit() {
        return creditLimit.subtract(outstandingAmount);
    }

    /**
     * Calculates credit utilization percentage.
     */
    public BigDecimal getCreditUtilization() {
        if (creditLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return outstandingAmount
                .divide(creditLimit, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
