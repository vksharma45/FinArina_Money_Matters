package com.portfolio.controller;

import com.portfolio.dto.request.PortfolioRequest;
import com.portfolio.dto.response.ApiResponse;
import com.portfolio.dto.response.AssetGroupPerformanceResponse;
import com.portfolio.dto.response.PortfolioResponse;
import com.portfolio.dto.response.PortfolioSummaryResponse;
import com.portfolio.service.AssetGroupService;
import com.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POST   /portfolios
 * GET    /portfolios
 * GET    /portfolios/{portfolioId}
 * GET    /portfolios/{portfolioId}/summary
 * DELETE /portfolios/{portfolioId}
 * GET    /portfolios/{portfolioId}/asset-groups/performance
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final AssetGroupService assetGroupService;

    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            @Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Portfolio created successfully",
                        portfolioService.createPortfolio(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getAllPortfolios() {
        return ResponseEntity.ok(ApiResponse.success("Portfolios retrieved successfully",
                portfolioService.getAllPortfolios()));
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolio(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Portfolio retrieved successfully",
                portfolioService.getPortfolio(portfolioId)));
    }

    @GetMapping("/{portfolioId}/summary")
    public ResponseEntity<ApiResponse<PortfolioSummaryResponse>> getPortfolioSummary(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Portfolio summary retrieved successfully",
                portfolioService.getPortfolioSummary(portfolioId)));
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio deleted successfully", null));
    }

    /** GET /portfolios/{portfolioId}/asset-groups/performance */
    @GetMapping("/{portfolioId}/asset-groups/performance")
    public ResponseEntity<ApiResponse<List<AssetGroupPerformanceResponse>>> getAssetGroupPerformance(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Asset group performance retrieved successfully",
                assetGroupService.getAllGroupPerformanceForPortfolio(portfolioId)));
    }
}
