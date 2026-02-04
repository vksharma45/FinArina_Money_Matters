package com.portfolio.service;

import com.portfolio.dto.request.AssetBuyRequest;
import com.portfolio.dto.request.AssetRequest;
import com.portfolio.dto.request.AssetUpdateRequest;
import com.portfolio.dto.response.AssetPerformanceResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetGroup;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.StockCategory;
import com.portfolio.exception.InvalidRequestException;
import com.portfolio.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void createHolding_success_and_recordsBuy() {
        AssetRequest req = new AssetRequest();
        req.setAssetName("HoldingAsset");
        req.setIsWishlist(false);
        req.setBuyPrice(new BigDecimal("50"));
        req.setQuantity(new BigDecimal("2"));

        Portfolio p = new Portfolio();
        p.setPortfolioId(1L);
        when(portfolioService.findPortfolioById(1L)).thenReturn(p);

        Asset saved = new Asset();
        saved.setAssetId(11L);
        saved.setPortfolio(p);
        saved.setQuantity(req.getQuantity());
        saved.setBuyPrice(req.getBuyPrice());

        when(assetRepository.save(any(Asset.class))).thenReturn(saved);

        var resp = assetService.createAsset(1L, req);
        assertEquals(11L, resp.getAssetId());
        verify(assetHistoryService, times(1)).recordBuy(any(Asset.class), any(), any(), any());
    }

    @Test
    void createStock_requiresCategory() {
        AssetRequest req = new AssetRequest();
        req.setAssetName("StockAsset");
        req.setIsWishlist(false);
        req.setAssetType(AssetType.STOCK);
        req.setBuyPrice(new BigDecimal("20"));
        req.setQuantity(new BigDecimal("1"));

        when(portfolioService.findPortfolioById(1L)).thenReturn(new Portfolio());

        assertThrows(InvalidRequestException.class, () -> assetService.createAsset(1L, req));

        // now provide category
        req.setStockCategoryId(2L);
        StockCategory cat = new StockCategory();
        cat.setCategoryId(2L);
        when(stockCategoryService.findCategoryById(2L)).thenReturn(cat);

        Asset saved = new Asset();
        saved.setAssetId(12L);
        saved.setPortfolio(new Portfolio());
        when(assetRepository.save(any(Asset.class))).thenReturn(saved);

        var resp = assetService.createAsset(1L, req);
        assertEquals(12L, resp.getAssetId());
    }

    @Test
    void getAsset_and_listings() {
        Asset a = new Asset();
        a.setAssetId(21L);
        when(assetRepository.findById(21L)).thenReturn(Optional.of(a));
        assertEquals(21L, assetService.getAsset(21L).getAssetId());

        when(assetRepository.findByPortfolioPortfolioId(2L)).thenReturn(List.of(a));
        assertFalse(assetService.getPortfolioAssets(2L).isEmpty());

        when(assetRepository.findByPortfolioPortfolioIdAndWishlistTrue(3L)).thenReturn(List.of());
        assertNotNull(assetService.getWishlistAssets(3L));
    }

    @Test
    void getPerformance_returnsValues() {
        Asset a = new Asset();
        a.setAssetId(31L);
        a.setAssetName("Perf");
        a.setAssetType(AssetType.CASH);
        a.setQuantity(new BigDecimal("2"));
        a.setBuyPrice(new BigDecimal("10"));
        a.setCurrentPrice(new BigDecimal("15"));
        a.setWishlist(false);
        when(assetRepository.findById(31L)).thenReturn(Optional.of(a));

        AssetPerformanceResponse resp = assetService.getPerformance(31L);
        assertEquals(31L, resp.getAssetId());
        assertEquals(new BigDecimal("20"), resp.getInvestedValue());
    }

    @Test
    void updateAsset_recordsHistory_onChanges() {
        Asset a = new Asset();
        a.setAssetId(41L);
        a.setQuantity(new BigDecimal("5"));
        a.setCurrentPrice(new BigDecimal("10"));

        when(assetRepository.findById(41L)).thenReturn(Optional.of(a));
        when(assetRepository.save(any(Asset.class))).thenReturn(a);

        AssetUpdateRequest req = new AssetUpdateRequest();
        req.setQuantity(new BigDecimal("7"));
        req.setCurrentPrice(new BigDecimal("12"));

        var resp = assetService.updateAsset(41L, req);
        verify(assetHistoryService, times(1)).recordQuantityUpdate(any(Asset.class), any(), any());
        verify(assetHistoryService, times(1)).recordPriceUpdate(any(Asset.class), any(), any());
        assertEquals(41L, resp.getAssetId());
    }

    @Test
    void deleteAsset_detachesGroups_and_deletes() {
        Asset a = new Asset();
        a.setAssetId(51L);
        AssetGroup g = new AssetGroup();
        g.setGroupId(1L);
        g.getAssets().add(a);
        a.getGroups().add(g);

        when(assetRepository.findById(51L)).thenReturn(Optional.of(a));
        doNothing().when(assetRepository).delete(a);

        assetService.deleteAsset(51L);
        verify(assetRepository, times(1)).delete(a);
        assertTrue(g.getAssets().isEmpty());
    }
}
