package com.portfolio.controller;

import com.portfolio.dto.request.AssetGroupRequest;
import com.portfolio.dto.response.ApiResponse;
import com.portfolio.dto.response.AssetGroupPerformanceResponse;
import com.portfolio.dto.response.AssetGroupResponse;
import com.portfolio.service.AssetGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spec routes:
 *   POST   /asset-groups                                     — create
 *   GET    /asset-groups                                     — list all
 *   GET    /asset-groups/{groupId}                           — single
 *   PUT    /asset-groups/{groupId}                           — update
 *   DELETE /asset-groups/{groupId}                           — delete
 *   GET    /asset-groups/{groupId}/performance?portfolioId=  — group performance scoped to portfolio
 */
@RestController
@RequestMapping("/asset-groups")
@RequiredArgsConstructor
@Slf4j
public class AssetGroupController {

    private final AssetGroupService assetGroupService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssetGroupResponse>> createGroup(
            @Valid @RequestBody AssetGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset group created successfully",
                        assetGroupService.createGroup(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetGroupResponse>>> getAllGroups() {
        return ResponseEntity.ok(ApiResponse.success("Asset groups retrieved successfully",
                assetGroupService.getAllGroups()));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<AssetGroupResponse>> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success("Asset group retrieved successfully",
                assetGroupService.getGroup(groupId)));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<AssetGroupResponse>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody AssetGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Asset group updated successfully",
                assetGroupService.updateGroup(groupId, request)));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable Long groupId) {
        assetGroupService.deleteGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("Asset group deleted successfully", null));
    }

    /**
     * GET /asset-groups/{groupId}/performance?portfolioId=X
     * Performance for this group scoped to a specific portfolio.
     */
    @GetMapping("/{groupId}/performance")
    public ResponseEntity<ApiResponse<AssetGroupPerformanceResponse>> getGroupPerformance(
            @PathVariable Long groupId,
            @RequestParam Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Group performance retrieved successfully",
                assetGroupService.getGroupPerformance(groupId, portfolioId)));
    }
}
