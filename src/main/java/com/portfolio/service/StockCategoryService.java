package com.portfolio.service;

import com.portfolio.dto.request.StockCategoryRequest;
import com.portfolio.dto.response.StockCategoryPerformanceResponse;
import com.portfolio.dto.response.StockCategoryResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.StockCategory;
import com.portfolio.exception.ResourceAlreadyExistsException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.StockCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StockCategoryService {

    private final StockCategoryRepository stockCategoryRepository;
    private final AssetRepository assetRepository;
    private final PortfolioService portfolioService;

    // ---------------------------------------------------------------
    // CRUD  (returns DTO, not raw entity)
    // ---------------------------------------------------------------

    @Transactional
    public StockCategoryResponse createCategory(StockCategoryRequest request) {
        if (stockCategoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new ResourceAlreadyExistsException(
                    "Category '" + request.getCategoryName() + "' already exists.");
        }
        StockCategory cat = new StockCategory();
        cat.setCategoryName(request.getCategoryName());
        cat.setDescription(request.getDescription());
        StockCategory saved = stockCategoryRepository.save(cat);
        log.info("StockCategory created: {}", saved.getCategoryId());
        return mapCategoryToResponse(saved);
    }

    public List<StockCategoryResponse> getAllCategories() {
        return stockCategoryRepository.findAll().stream()
                .map(this::mapCategoryToResponse).toList();
    }

    public StockCategoryResponse getCategory(Long categoryId) {
        return mapCategoryToResponse(findCategoryById(categoryId));
    }

    // ---------------------------------------------------------------
    // PERFORMANCE  (holding stocks only, scoped to a portfolio)
    // ---------------------------------------------------------------

    /**
     * GET /stock-categories/performance/portfolio/{portfolioId}
     * All categories that have at least one holding stock in the portfolio.
     */
    public List<StockCategoryPerformanceResponse> getCategoryPerformance(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);

        List<Asset> holdingStocks = assetRepository.findHoldingStocksByPortfolio(portfolioId);

        // group by category
        Map<Long, List<Asset>> byCategory = new HashMap<>();
        for (Asset a : holdingStocks) {
            if (a.getStockCategory() != null) {
                byCategory.computeIfAbsent(a.getStockCategory().getCategoryId(),
                        k -> new ArrayList<>()).add(a);
            }
        }

        List<StockCategoryPerformanceResponse> result = new ArrayList<>();
        for (var entry : byCategory.entrySet()) {
            result.add(calcPerformance(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * GET /stock-categories/{categoryId}/performance?portfolioId=X
     */
    public StockCategoryPerformanceResponse getCategoryPerformanceById(Long portfolioId, Long categoryId) {
        portfolioService.findPortfolioById(portfolioId);
        findCategoryById(categoryId);

        List<Asset> holdingStocks = assetRepository
                .findHoldingStocksByPortfolioAndCategory(portfolioId, categoryId);

        if (holdingStocks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No holding stocks for category " + categoryId + " in portfolio " + portfolioId);
        }
        return calcPerformance(categoryId, holdingStocks);
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    public StockCategory findCategoryById(Long categoryId) {
        return stockCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockCategory not found with ID: " + categoryId));
    }

    private StockCategoryPerformanceResponse calcPerformance(Long categoryId, List<Asset> assets) {
        StockCategory cat = findCategoryById(categoryId);

        BigDecimal totalInvested = assets.stream()
                .map(Asset::getInvestedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentValue = assets.stream()
                .map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);
        BigDecimal percentageReturn = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            percentageReturn = absoluteReturn
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return StockCategoryPerformanceResponse.builder()
                .categoryId(cat.getCategoryId())
                .categoryName(cat.getCategoryName())
                .description(cat.getDescription())
                .totalInvested(totalInvested)
                .currentValue(currentValue)
                .absoluteReturn(absoluteReturn)
                .percentageReturn(percentageReturn)
                .stockCount(assets.size())
                .build();
    }

    private StockCategoryResponse mapCategoryToResponse(StockCategory cat) {
        return StockCategoryResponse.builder()
                .categoryId(cat.getCategoryId())
                .categoryName(cat.getCategoryName())
                .description(cat.getDescription())
                .build();
    }
}
