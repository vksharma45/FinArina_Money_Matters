package com.portfolio.controller;

import com.portfolio.dto.request.AssetBuyRequest;
import com.portfolio.dto.request.AssetGroupMemberRequest;
import com.portfolio.dto.request.AssetRequest;
import com.portfolio.dto.request.AssetUpdateRequest;
import com.portfolio.dto.response.ApiResponse;
import com.portfolio.dto.response.AssetGroupResponse;
import com.portfolio.dto.response.AssetHistoryResponse;
import com.portfolio.dto.response.AssetPerformanceResponse;
import com.portfolio.dto.response.AssetResponse;
import com.portfolio.service.AssetGroupService;
import com.portfolio.service.AssetHistoryService;
import com.portfolio.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spec routes implemented here:
 *
 *   POST   /portfolios/{portfolioId}/assets          — create asset
 *   GET    /portfolios/{portfolioId}/assets          — all assets
 *   GET    /portfolios/{portfolioId}/wishlist        — wishlist only
 *   GET    /assets/{assetId}                         — single asset
 *   PUT    /assets/{assetId}                         — update asset
 *   DELETE /assets/{assetId}                         — delete asset
 *   POST   /assets/{assetId}/buy                    — wishlist → holding
 *   GET    /assets/{assetId}/history                 — audit trail
 *   GET    /assets/{assetId}/performance             — individual performance
 *   POST   /assets/{assetId}/groups                  — add groups
 *   PUT    /assets/{assetId}/groups                  — replace groups
 *   DELETE /assets/{assetId}/groups/{groupId}        — remove one group
 *   GET    /assets/{assetId}/groups                  — list groups
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;
    private final AssetHistoryService assetHistoryService;
    private final AssetGroupService assetGroupService;


    // CREATE  (nested under portfolio)
    @PostMapping("/portfolios/{portfolioId}/assets")
    public ResponseEntity<ApiResponse<AssetResponse>> createAsset(
            @PathVariable Long portfolioId,
            @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset created successfully",
                        assetService.createAsset(portfolioId, request)));
    }

    // LIST  (nested under portfolio)
    @GetMapping("/portfolios/{portfolioId}/assets")
    public ResponseEntity<ApiResponse<List<AssetResponse>>> getPortfolioAssets(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Assets retrieved successfully",
                assetService.getPortfolioAssets(portfolioId)));
    }

    @GetMapping("/portfolios/{portfolioId}/wishlist")
    public ResponseEntity<ApiResponse<List<AssetResponse>>> getWishlist(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully",
                assetService.getWishlistAssets(portfolioId)));
    }

    // SINGLE ASSET

    @GetMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<AssetResponse>> getAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(ApiResponse.success("Asset retrieved successfully",
                assetService.getAsset(assetId)));
    }

    @PutMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<AssetResponse>> updateAsset(
            @PathVariable Long assetId,
            @RequestBody AssetUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Asset updated successfully",
                assetService.updateAsset(assetId, request)));
    }

    @DeleteMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long assetId) {
        assetService.deleteAsset(assetId);
        return ResponseEntity.ok(ApiResponse.success("Asset deleted successfully", null));
    }

    // WISHLIST - HOLDING
    @PostMapping("/assets/{assetId}/buy")
    public ResponseEntity<ApiResponse<AssetResponse>> buyAsset(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetBuyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Asset purchased successfully",
                assetService.buyAsset(assetId, request)));
    }

    // HISTORY
    @GetMapping("/assets/{assetId}/history")
    public ResponseEntity<ApiResponse<List<AssetHistoryResponse>>> getAssetHistory(
            @PathVariable Long assetId) {
        // validate asset exists
        assetService.findAssetById(assetId);
        return ResponseEntity.ok(ApiResponse.success("Asset history retrieved successfully",
                assetHistoryService.getHistory(assetId)));
    }


    // PERFORMANCE
    @GetMapping("/assets/{assetId}/performance")
    public ResponseEntity<ApiResponse<AssetPerformanceResponse>> getAssetPerformance(
            @PathVariable Long assetId) {
        return ResponseEntity.ok(ApiResponse.success("Asset performance retrieved successfully",
                assetService.getPerformance(assetId)));
    }

    // ASSET - GROUP MAPPING
    @PostMapping("/assets/{assetId}/groups")
    public ResponseEntity<ApiResponse<List<AssetGroupResponse>>> addGroups(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetGroupMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Groups added successfully",
                assetGroupService.addGroupsToAsset(assetId, request)));
    }

    @PutMapping("/assets/{assetId}/groups")
    public ResponseEntity<ApiResponse<List<AssetGroupResponse>>> replaceGroups(
            @PathVariable Long assetId,
            @Valid @RequestBody AssetGroupMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Groups updated successfully",
                assetGroupService.replaceGroupsForAsset(assetId, request)));
    }

    @DeleteMapping("/assets/{assetId}/groups/{groupId}")
    public ResponseEntity<ApiResponse<Void>> removeGroup(
            @PathVariable Long assetId,
            @PathVariable Long groupId) {
        assetGroupService.removeAssetFromGroup(assetId, groupId);
        return ResponseEntity.ok(ApiResponse.success("Asset removed from group successfully", null));
    }

    @GetMapping("/assets/{assetId}/groups")
    public ResponseEntity<ApiResponse<List<AssetGroupResponse>>> getGroups(
            @PathVariable Long assetId) {
        return ResponseEntity.ok(ApiResponse.success("Groups retrieved successfully",
                assetGroupService.getGroupsForAsset(assetId)));
    }
}
