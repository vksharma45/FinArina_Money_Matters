package com.portfolio.dto.response;

import com.portfolio.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {

    private Long assetId;
    private Long portfolioId;
    private String assetName;
    private AssetType assetType;
    private BigDecimal quantity;
    private BigDecimal buyPrice;          // null for wishlist
    private BigDecimal currentPrice;
    private boolean isWishlist;
    private BigDecimal investedValue;     // 0 for wishlist
    private BigDecimal currentValue;
    private BigDecimal absoluteReturn;    // 0 for wishlist
    private BigDecimal percentageReturn;  // 0 for wishlist
    private String stockCategoryName;
    private List<String> groupNames;
}
