package com.portfolio.dto.request;

import com.portfolio.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for updating an existing asset (PUT).
 * All fields are optional — only supplied fields are updated.
 * assetName and assetType can be changed.
 * quantity and currentPrice can be changed (each change is recorded in history).
 * buyPrice and isWishlist are NOT updatable here; use /buy endpoint for that conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdateRequest {

    private String assetName;
    private AssetType assetType;
    private BigDecimal quantity;
    private BigDecimal currentPrice;
    private Long stockCategoryId;
}
