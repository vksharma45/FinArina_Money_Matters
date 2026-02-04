package com.portfolio.service;

import com.portfolio.dto.request.AssetBuyRequest;
import com.portfolio.dto.request.AssetRequest;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import com.portfolio.exception.InvalidRequestException;
import com.portfolio.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private StockCategoryService stockCategoryService;

    @Mock
    private AssetHistoryService assetHistoryService;

    @InjectMocks
    private AssetService assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWishlist_assetWithBuyPrice_throwsInvalidRequest() {
        AssetRequest req = new AssetRequest();
        req.setAssetName("WishlistAsset");
        req.setIsWishlist(true);
        req.setBuyPrice(new BigDecimal("10"));

        when(portfolioService.findPortfolioById(1L)).thenReturn(new Portfolio());

        assertThrows(InvalidRequestException.class, () -> assetService.createAsset(1L, req));
    }

    @Test
    void buyAsset_alreadyHolding_throwsInvalidRequest() {
        Asset asset = new Asset();
        asset.setAssetId(5L);
        asset.setWishlist(false);

        when(assetRepository.findById(5L)).thenReturn(Optional.of(asset));

        AssetBuyRequest req = new AssetBuyRequest();
        req.setQuantity(new BigDecimal("1"));
        req.setBuyPrice(new BigDecimal("100"));

        assertThrows(InvalidRequestException.class, () -> assetService.buyAsset(5L, req));
    }
}
