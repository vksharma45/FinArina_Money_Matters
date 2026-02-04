package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

/**
 * Asset entity representing an investment in a portfolio.
 *
 * Two states:
 *   Holding  — isWishlist = false, buyPrice is set, participates in value/return calculations.
 *   Wishlist — isWishlist = true,  buyPrice is null, excluded from all financial calculations.
 *
 * For STOCK type assets, stockCategory is mandatory.
 */
@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "asset_name", nullable = false, length = 100)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 20)
    private AssetType assetType;

    @Column(name = "quantity", precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    /**
     * Null when isWishlist = true. Set when the asset is purchased (holding).
     */
    @Column(name = "buy_price", precision = 15, scale = 2)
    private BigDecimal buyPrice;

    @Column(name = "current_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentPrice;

    /**
     * true  → wishlist item, not yet purchased.
     * false → holding, buyPrice is populated.
     */
    @Column(name = "is_wishlist", nullable = false)
    private boolean wishlist;

    /**
     * Mandatory only for STOCK type. Null for all other asset types.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StockCategory stockCategory;

    /**
     * All groups this asset belongs to (Many-to-Many, inverse side).
     */
    @ManyToMany(mappedBy = "assets")
    private Set<AssetGroup> groups = new HashSet<>();

    // ---------------------------------------------------------------
    // Derived calculations — only meaningful for holding assets.
    // Callers must check isWishlist before using these.
    // ---------------------------------------------------------------

    public BigDecimal getInvestedValue() {
        if (buyPrice == null) return BigDecimal.ZERO;
        return quantity.multiply(buyPrice);
    }

    public BigDecimal getCurrentValue() {
        return quantity.multiply(currentPrice);
    }

    public BigDecimal getAbsoluteReturn() {
        return getCurrentValue().subtract(getInvestedValue());
    }

    public BigDecimal getPercentageReturn() {
        BigDecimal invested = getInvestedValue();
        if (invested.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return getAbsoluteReturn()
                .divide(invested, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
