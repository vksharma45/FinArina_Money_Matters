package com.portfolio.repository;

import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /** All assets (holding + wishlist) in a portfolio. */
    List<Asset> findByPortfolioPortfolioId(Long portfolioId);

    /** Only holding assets in a portfolio. */
    List<Asset> findByPortfolioPortfolioIdAndWishlistFalse(Long portfolioId);

    /** Only wishlist assets in a portfolio. */
    List<Asset> findByPortfolioPortfolioIdAndWishlistTrue(Long portfolioId);

    /** Holding assets filtered by type. */
    List<Asset> findByPortfolioPortfolioIdAndAssetTypeAndWishlistFalse(
            Long portfolioId, AssetType assetType);

    /** Holding stocks in a portfolio for a given category. */
    @Query("SELECT a FROM Asset a WHERE a.portfolio.portfolioId = :portfolioId " +
           "AND a.assetType = 'STOCK' AND a.stockCategory.categoryId = :categoryId " +
           "AND a.wishlist = false")
    List<Asset> findHoldingStocksByPortfolioAndCategory(
            @Param("portfolioId") Long portfolioId,
            @Param("categoryId") Long categoryId);

    /** All holding stocks in a portfolio, ordered by category name. */
    @Query("SELECT a FROM Asset a WHERE a.portfolio.portfolioId = :portfolioId " +
           "AND a.assetType = 'STOCK' AND a.wishlist = false " +
           "ORDER BY a.stockCategory.categoryName")
    List<Asset> findHoldingStocksByPortfolio(@Param("portfolioId") Long portfolioId);
}
