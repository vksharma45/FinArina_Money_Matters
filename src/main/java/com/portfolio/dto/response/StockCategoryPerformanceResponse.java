package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Aggregated performance for all HOLDING stocks in one category within a portfolio.
 * Wishlist stocks are excluded.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCategoryPerformanceResponse {

    private Long categoryId;
    private String categoryName;
    private String description;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal absoluteReturn;
    private BigDecimal percentageReturn;
    private Integer stockCount;
}
