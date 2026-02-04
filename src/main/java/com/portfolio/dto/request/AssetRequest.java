package com.portfolio.dto.request;

import com.portfolio.entity.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating a new asset.
 *
 * If isWishlist = true  → buyPrice must be null (validation enforced in service).
 * If isWishlist = false → buyPrice must be provided and positive.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest {

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    /** Null for wishlist assets. */
    private BigDecimal buyPrice;

    @NotNull(message = "Current price is required")
    @Positive(message = "Current price must be positive")
    private BigDecimal currentPrice;

    /**
     * true  → create as wishlist (buyPrice ignored / must be null).
     * false → create as holding (buyPrice mandatory).
     * Defaults to false if not supplied.
     */
    private Boolean isWishlist = false;

    /** Mandatory only when assetType = STOCK. */
    private Long stockCategoryId;
}
