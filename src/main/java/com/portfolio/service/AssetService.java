package com.portfolio.service;

import com.portfolio.dto.request.AssetBuyRequest;
import com.portfolio.dto.request.AssetRequest;
import com.portfolio.dto.request.AssetUpdateRequest;
import com.portfolio.dto.response.AssetPerformanceResponse;
import com.portfolio.dto.response.AssetResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetGroup;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.StockCategory;
import com.portfolio.exception.InvalidRequestException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetService {

    private final AssetRepository assetRepository;
    private final PortfolioService portfolioService;
    private final StockCategoryService stockCategoryService;
    private final AssetHistoryService assetHistoryService;

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    /**
     * POST /portfolios/{portfolioId}/assets
     *
     * Rules enforced:
     *   - isWishlist=true  → buyPrice must be null
     *   - isWishlist=false → buyPrice must be provided & positive
     *   - assetType=STOCK  → stockCategoryId mandatory
     *   - BUY history recorded only when isWishlist=false
     */
    @Transactional
    public AssetResponse createAsset(Long portfolioId, AssetRequest request) {
        log.info("Creating asset '{}' in portfolio {}", request.getAssetName(), portfolioId);

        Portfolio portfolio = portfolioService.findPortfolioById(portfolioId);
        boolean isWishlist = Boolean.TRUE.equals(request.getIsWishlist());

        // --- wishlist / holding validation ---
        if (isWishlist && request.getBuyPrice() != null) {
            throw new InvalidRequestException("buyPrice must be null for wishlist assets.");
        }
        if (!isWishlist) {
            if (request.getBuyPrice() == null) {
                throw new InvalidRequestException("buyPrice is required for holding assets.");
            }
            if (request.getBuyPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidRequestException("buyPrice must be positive.");
            }
        }

        // --- stock category validation ---
        StockCategory stockCategory = null;
        if (request.getAssetType() == AssetType.STOCK) {
            if (request.getStockCategoryId() == null) {
                throw new InvalidRequestException("stockCategoryId is mandatory for STOCK assets.");
            }
            stockCategory = stockCategoryService.findCategoryById(request.getStockCategoryId());
        }

        Asset asset = new Asset();
        asset.setPortfolio(portfolio);
        asset.setAssetName(request.getAssetName());
        asset.setAssetType(request.getAssetType());
        asset.setQuantity(request.getQuantity());
        asset.setBuyPrice(request.getBuyPrice());
        asset.setCurrentPrice(request.getCurrentPrice());
        asset.setWishlist(isWishlist);
        asset.setStockCategory(stockCategory);

        Asset saved = assetRepository.save(asset);

        // record BUY history only for holdings
        if (!isWishlist) {
            assetHistoryService.recordBuy(saved, saved.getQuantity(), saved.getBuyPrice(),
                    "Initial purchase");
        }

        log.info("Asset created with ID: {}", saved.getAssetId());
        return mapToResponse(saved);
    }

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** GET /assets/{assetId} */
    public AssetResponse getAsset(Long assetId) {
        return mapToResponse(findAssetById(assetId));
    }

    /** GET /portfolios/{portfolioId}/assets  — all (holding + wishlist) */
    public List<AssetResponse> getPortfolioAssets(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);
        return assetRepository.findByPortfolioPortfolioId(portfolioId)
                .stream().map(this::mapToResponse).toList();
    }

    /** GET /portfolios/{portfolioId}/wishlist */
    public List<AssetResponse> getWishlistAssets(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);
        return assetRepository.findByPortfolioPortfolioIdAndWishlistTrue(portfolioId)
                .stream().map(this::mapToResponse).toList();
    }

    /** GET /assets/{assetId}/performance */
    public AssetPerformanceResponse getPerformance(Long assetId) {
        Asset a = findAssetById(assetId);
        return AssetPerformanceResponse.builder()
                .assetId(a.getAssetId())
                .assetName(a.getAssetName())
                .assetType(a.getAssetType())
                .isWishlist(a.isWishlist())
                .quantity(a.getQuantity())
                .buyPrice(a.getBuyPrice())
                .currentPrice(a.getCurrentPrice())
                .investedValue(a.getInvestedValue())
                .currentValue(a.getCurrentValue())
                .absoluteReturn(a.getAbsoluteReturn())
                .percentageReturn(a.getPercentageReturn())
                .build();
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------

    /**
     * PUT /assets/{assetId}
     * Only supplied (non-null) fields are applied.
     * quantity changes → QUANTITY_UPDATE history
     * currentPrice changes → PRICE_UPDATE history
     */
    @Transactional
    public AssetResponse updateAsset(Long assetId, AssetUpdateRequest req) {
        Asset asset = findAssetById(assetId);

        if (req.getAssetName() != null) {
            asset.setAssetName(req.getAssetName());
        }
        if (req.getAssetType() != null) {
            asset.setAssetType(req.getAssetType());
            // if switching TO stock, category becomes mandatory
            if (req.getAssetType() == AssetType.STOCK && asset.getStockCategory() == null
                    && req.getStockCategoryId() == null) {
                throw new InvalidRequestException("stockCategoryId is mandatory when assetType is STOCK.");
            }
        }
        if (req.getStockCategoryId() != null) {
            asset.setStockCategory(stockCategoryService.findCategoryById(req.getStockCategoryId()));
        }
        if (req.getQuantity() != null && req.getQuantity().compareTo(asset.getQuantity()) != 0) {
            assetHistoryService.recordQuantityUpdate(asset, asset.getQuantity(), req.getQuantity());
            asset.setQuantity(req.getQuantity());
        }
        if (req.getCurrentPrice() != null && req.getCurrentPrice().compareTo(asset.getCurrentPrice()) != 0) {
            assetHistoryService.recordPriceUpdate(asset, asset.getCurrentPrice(), req.getCurrentPrice());
            asset.setCurrentPrice(req.getCurrentPrice());
        }

        Asset updated = assetRepository.save(asset);
        log.info("Asset {} updated", assetId);
        return mapToResponse(updated);
    }

    // ---------------------------------------------------------------
    // WISHLIST → HOLDING CONVERSION
    // ---------------------------------------------------------------

    /**
     * POST /assets/{assetId}/buy
     * Converts a wishlist asset to a holding.
     * Records a BUY history entry.
     */
    @Transactional
    public AssetResponse buyAsset(Long assetId, AssetBuyRequest req) {
        Asset asset = findAssetById(assetId);

        if (!asset.isWishlist()) {
            throw new InvalidRequestException("Asset " + assetId + " is already a holding. Cannot buy again.");
        }

        BigDecimal quantity = req.getQuantity() != null ? req.getQuantity() : asset.getQuantity();

        asset.setWishlist(false);
        asset.setBuyPrice(req.getBuyPrice());
        asset.setQuantity(quantity);

        Asset updated = assetRepository.save(asset);

        assetHistoryService.recordBuy(updated, quantity, req.getBuyPrice(),
                req.getRemarks() != null ? req.getRemarks() : "Converted from wishlist to holding");

        log.info("Asset {} converted from wishlist to holding", assetId);
        return mapToResponse(updated);
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    /**
     * DELETE /assets/{assetId}
     * Clears group memberships first, then deletes the asset.
     * History is NOT deleted (append-only rule).
     */
    @Transactional
    public void deleteAsset(Long assetId) {
        Asset asset = findAssetById(assetId);

        // detach from all groups to avoid orphaned join-table rows
        for (AssetGroup group : asset.getGroups()) {
            group.getAssets().remove(asset);
        }
        asset.getGroups().clear();

        assetRepository.delete(asset);
        log.info("Asset {} deleted", assetId);
    }

    // ---------------------------------------------------------------
    // SHARED HELPERS
    // ---------------------------------------------------------------

    public Asset findAssetById(Long assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));
    }

    AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .assetId(asset.getAssetId())
                .portfolioId(asset.getPortfolio().getPortfolioId())
                .assetName(asset.getAssetName())
                .assetType(asset.getAssetType())
                .quantity(asset.getQuantity())
                .buyPrice(asset.getBuyPrice())
                .currentPrice(asset.getCurrentPrice())
                .isWishlist(asset.isWishlist())
                .investedValue(asset.getInvestedValue())
                .currentValue(asset.getCurrentValue())
                .absoluteReturn(asset.getAbsoluteReturn())
                .percentageReturn(asset.getPercentageReturn())
                .stockCategoryName(asset.getStockCategory() != null
                        ? asset.getStockCategory().getCategoryName() : null)
                .groupNames(asset.getGroups().stream()
                        .map(AssetGroup::getGroupName)
                        .sorted()
                        .toList())
                .build();
    }
}
