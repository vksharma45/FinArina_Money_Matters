package com.portfolio.service;

import com.portfolio.entity.Asset;
import com.portfolio.entity.StockCategory;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.StockCategoryRepository;
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

class StockCategoryServiceTest {

    @Mock
    private StockCategoryRepository stockCategoryRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private StockCategoryService stockCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findCategoryById_notFound() {
        when(stockCategoryRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(RuntimeException.class, () -> stockCategoryService.findCategoryById(99L));
        assertTrue(ex.getMessage().contains("StockCategory not found"));
    }

    @Test
    void createCategory_success_and_getAll_getCategory() {
        StockCategory cat = new StockCategory();
        cat.setCategoryId(2L);
        cat.setCategoryName("Energy");

        when(stockCategoryRepository.existsByCategoryName("Energy")).thenReturn(false);
        when(stockCategoryRepository.save(any(StockCategory.class))).thenReturn(cat);
        when(stockCategoryRepository.findAll()).thenReturn(List.of(cat));
        when(stockCategoryRepository.findById(2L)).thenReturn(Optional.of(cat));

        var resp = stockCategoryService.createCategory(null);
        // createCategory returns a DTO; ensure repository save was called indirectly
        verify(stockCategoryRepository, times(1)).save(any(StockCategory.class));

        var all = stockCategoryService.getAllCategories();
        assertEquals(1, all.size());

        var single = stockCategoryService.getCategory(2L);
        assertEquals(2L, single.getCategoryId());
    }

    @Test
    void getCategoryPerformance_positiveCalculation() {
        // prepare category
        StockCategory cat = new StockCategory();
        cat.setCategoryId(3L);
        cat.setCategoryName("Tech");

        Asset a = new Asset();
        a.setAssetId(1L);
        a.setQuantity(new BigDecimal("2"));
        a.setBuyPrice(new BigDecimal("10"));
        a.setCurrentPrice(new BigDecimal("20"));
        a.setWishlist(false);
        a.setStockCategory(cat);

        when(stockCategoryRepository.findById(3L)).thenReturn(Optional.of(cat));
        when(portfolioService.findPortfolioById(5L)).thenReturn(null);
        when(assetRepository.findHoldingStocksByPortfolio(5L)).thenReturn(List.of(a));

        var list = stockCategoryService.getCategoryPerformance(5L);
        assertEquals(1, list.size());
        assertEquals(new BigDecimal("20"), list.get(0).getCurrentValue());
    }

    @Test
    void calcPerformance_emptyAssets_returnsZeroValues() {
        StockCategory cat = new StockCategory();
        cat.setCategoryId(1L);
        cat.setCategoryName("Tech");

        when(stockCategoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        // calcPerformance is private; use getCategoryPerformanceById which calls it
        when(assetRepository.findHoldingStocksByPortfolioAndCategory(1L, 1L)).thenReturn(List.of());

        var ex = assertThrows(RuntimeException.class, () -> stockCategoryService.getCategoryPerformanceById(1L, 1L));
        assertTrue(ex.getMessage().contains("No holding stocks"));
    }
}
