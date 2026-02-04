package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Aggregated performance for all HOLDING assets in a group
 * (scoped to a specific portfolio at query time).
 * Wishlist assets are excluded.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetGroupPerformanceResponse {

    private Long groupId;
    private String groupName;
    private Integer holdingCount;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal absoluteReturn;
    private BigDecimal percentageReturn;
}
