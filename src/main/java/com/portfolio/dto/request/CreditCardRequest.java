package com.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for adding a new credit card to a portfolio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardRequest {

    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;

    @NotBlank(message = "Card name is required")
    private String cardName;

    @NotNull(message = "Credit limit is required")
    @Positive(message = "Credit limit must be positive")
    private BigDecimal creditLimit;

    @NotNull(message = "Outstanding amount is required")
    @PositiveOrZero(message = "Outstanding amount cannot be negative")
    private BigDecimal outstandingAmount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
}
