package com.portfolio.controller;

import com.portfolio.dto.request.StockCategoryRequest;
import com.portfolio.dto.response.ApiResponse;
import com.portfolio.dto.response.StockCategoryPerformanceResponse;
import com.portfolio.dto.response.StockCategoryResponse;
import com.portfolio.service.StockCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POST   /stock-categories
 * GET    /stock-categories
 * GET    /stock-categories/{categoryId}
 * GET    /stock-categories/performance/portfolio/{portfolioId}     — all categories
 * GET    /stock-categories/{categoryId}/performance?portfolioId=X  — single category
 */
@RestController
@RequestMapping("/stock-categories")
@RequiredArgsConstructor
@Slf4j
public class StockCategoryController {

    private final StockCategoryService stockCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<StockCategoryResponse>> createCategory(
            @Valid @RequestBody StockCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock category created successfully",
                        stockCategoryService.createCategory(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockCategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success("Stock categories retrieved successfully",
                stockCategoryService.getAllCategories()));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<StockCategoryResponse>> getCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success("Stock category retrieved successfully",
                stockCategoryService.getCategory(categoryId)));
    }

    /** All-categories performance for one portfolio. */
    @GetMapping("/performance/portfolio/{portfolioId}")
    public ResponseEntity<ApiResponse<List<StockCategoryPerformanceResponse>>> getCategoryPerformance(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Category performance retrieved successfully",
                stockCategoryService.getCategoryPerformance(portfolioId)));
    }

    /** Single-category performance scoped to a portfolio. */
    @GetMapping("/{categoryId}/performance")
    public ResponseEntity<ApiResponse<StockCategoryPerformanceResponse>> getCategoryPerformanceById(
            @PathVariable Long categoryId,
            @RequestParam Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Category performance retrieved successfully",
                stockCategoryService.getCategoryPerformanceById(portfolioId, categoryId)));
    }
}
