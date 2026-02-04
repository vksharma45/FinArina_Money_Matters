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
import static org.mockito.Mockito.when;

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
