package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for portfolio summary including total value, returns, and asset allocation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryResponse {

    private Long portfolioId;
    private String portfolioName;
    private BigDecimal totalInvestedAmount;
    private BigDecimal currentPortfolioValue;
    private BigDecimal absoluteReturn;
    private BigDecimal percentageReturn;
    
    /**
     * Asset allocation map showing distribution by asset type.
     * Key: Asset type (e.g., "STOCK", "MUTUAL_FUND")
     * Value: Percentage of total portfolio value
     */
    private Map<String, BigDecimal> assetAllocation;
}
