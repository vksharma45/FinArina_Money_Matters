package com.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for converting a wishlist asset to a holding (POST /assets/{id}/buy).
 * The caller supplies the price they paid and (optionally) the quantity actually bought.
 * If quantity is null, the existing quantity on the asset is used as-is.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetBuyRequest {

    @NotNull(message = "Buy price is required")
    @Positive(message = "Buy price must be positive")
    private BigDecimal buyPrice;

    /** If null, keeps the quantity already on the asset. */
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    private String remarks;
}
