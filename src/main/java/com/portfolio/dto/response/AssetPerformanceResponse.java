package com.portfolio.dto.response;

import com.portfolio.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Individual asset performance view.
 * Only meaningful for holding assets; wishlist assets return zeros.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetPerformanceResponse {

    private Long assetId;
    private String assetName;
    private AssetType assetType;
    private boolean isWishlist;
    private BigDecimal quantity;
    private BigDecimal buyPrice;
    private BigDecimal currentPrice;
    private BigDecimal investedValue;
    private BigDecimal currentValue;
    private BigDecimal absoluteReturn;
    private BigDecimal percentageReturn;
}
