package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for credit card response with due date alert status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardResponse {

    private Long cardId;
    private Long portfolioId;
    private String cardName;
    private BigDecimal creditLimit;
    private BigDecimal outstandingAmount;
    private BigDecimal availableCredit;
    private BigDecimal creditUtilization;
    private LocalDate dueDate;
    private Long daysUntilDue;
    
    /**
     * Due status: "OK", "WARNING" (within 5 days), or "OVERDUE"
     */
    private String dueStatus;
    
    /**
     * Alert message based on due status
     */
    private String alertMessage;
}
