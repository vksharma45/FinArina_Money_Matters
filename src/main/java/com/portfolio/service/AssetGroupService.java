package com.portfolio.service;

import com.portfolio.dto.request.AssetGroupMemberRequest;
import com.portfolio.dto.request.AssetGroupRequest;
import com.portfolio.dto.response.AssetGroupPerformanceResponse;
import com.portfolio.dto.response.AssetGroupResponse;
import com.portfolio.dto.response.AssetResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetGroup;
import com.portfolio.exception.InvalidRequestException;
import com.portfolio.exception.ResourceAlreadyExistsException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.AssetGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for AssetGroup CRUD, asset↔group mapping, and group performance.
 *
 * Groups are global. Performance is always scoped by portfolioId at query time
 * and excludes wishlist assets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetGroupService {

    private final AssetGroupRepository assetGroupRepository;
    private final AssetService assetService;

    // ---------------------------------------------------------------
    // GROUP CRUD
    // ---------------------------------------------------------------

    /** POST /asset-groups */
    @Transactional
    public AssetGroupResponse createGroup(AssetGroupRequest request) {
        if (assetGroupRepository.existsByGroupName(request.getGroupName())) {
            throw new ResourceAlreadyExistsException(
                    "Group '" + request.getGroupName() + "' already exists.");
        }
        AssetGroup group = new AssetGroup();
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        AssetGroup saved = assetGroupRepository.save(group);
        log.info("Group created: {}", saved.getGroupId());
        return mapToResponse(saved);
    }

    /** GET /asset-groups */
    public List<AssetGroupResponse> getAllGroups() {
        return assetGroupRepository.findAllByOrderByGroupName()
                .stream().map(this::mapToResponse).toList();
    }

    /** GET /asset-groups/{groupId} */
    public AssetGroupResponse getGroup(Long groupId) {
        return mapToResponse(findGroupById(groupId));
    }

    /** PUT /asset-groups/{groupId} */
    @Transactional
    public AssetGroupResponse updateGroup(Long groupId, AssetGroupRequest request) {
        AssetGroup group = findGroupById(groupId);

        if (request.getGroupName() != null && !request.getGroupName().equals(group.getGroupName())) {
            if (assetGroupRepository.existsByGroupName(request.getGroupName())) {
                throw new ResourceAlreadyExistsException(
                        "Group '" + request.getGroupName() + "' already exists.");
            }
            group.setGroupName(request.getGroupName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        AssetGroup updated = assetGroupRepository.save(group);
        return mapToResponse(updated);
    }

    /** DELETE /asset-groups/{groupId} — clears membership, then deletes. */
    @Transactional
    public void deleteGroup(Long groupId) {
        AssetGroup group = findGroupById(groupId);
        group.getAssets().clear();
        assetGroupRepository.delete(group);
        log.info("Group {} deleted", groupId);
    }

    // ---------------------------------------------------------------
    // ASSET ↔ GROUP MAPPING  (called from AssetController routes)
    // ---------------------------------------------------------------

    /**
     * POST /assets/{assetId}/groups  — ADD group(s) to an asset.
     */
    @Transactional
    public List<AssetGroupResponse> addGroupsToAsset(Long assetId, AssetGroupMemberRequest req) {
        Asset asset = assetService.findAssetById(assetId);
        Set<AssetGroup> groups = resolveGroups(req.getGroupIds());

        for (AssetGroup g : groups) {
            g.addAsset(asset);
            assetGroupRepository.save(g);
        }

        // return the updated state of the asset's groups
        return asset.getGroups().stream()
                .map(this::mapToResponse).toList();
    }

    /**
     * PUT /assets/{assetId}/groups  — REPLACE the asset's groups with exactly these.
     */
    @Transactional
    public List<AssetGroupResponse> replaceGroupsForAsset(Long assetId, AssetGroupMemberRequest req) {
        Asset asset = assetService.findAssetById(assetId);

        // remove from all current groups
        for (AssetGroup g : new HashSet<>(asset.getGroups())) {
            g.removeAsset(asset);
            assetGroupRepository.save(g);
        }

        // add to the new set
        Set<AssetGroup> newGroups = resolveGroups(req.getGroupIds());
        for (AssetGroup g : newGroups) {
            g.addAsset(asset);
            assetGroupRepository.save(g);
        }

        return newGroups.stream().map(this::mapToResponse).toList();
    }

    /**
     * DELETE /assets/{assetId}/groups/{groupId}  — remove asset from one group.
     */
    @Transactional
    public void removeAssetFromGroup(Long assetId, Long groupId) {
        Asset asset = assetService.findAssetById(assetId);
        AssetGroup group = findGroupById(groupId);
        group.removeAsset(asset);
        assetGroupRepository.save(group);
    }

    /**
     * GET /assets/{assetId}/groups  — all groups the asset belongs to.
     */
    public List<AssetGroupResponse> getGroupsForAsset(Long assetId) {
        Asset asset = assetService.findAssetById(assetId);
        return asset.getGroups().stream()
                .map(this::mapToResponse).toList();
    }

    // ---------------------------------------------------------------
    // PERFORMANCE
    // ---------------------------------------------------------------

    /**
     * GET /asset-groups/{groupId}/performance?portfolioId=X
     *
     * Aggregates only HOLDING assets in this group that belong to the given portfolio.
     * Wishlist assets are excluded.
     */
    public AssetGroupPerformanceResponse getGroupPerformance(Long groupId, Long portfolioId) {
        AssetGroup group = findGroupById(groupId);

        List<Asset> holdingAssets = group.getAssets().stream()
                .filter(a -> a.getPortfolio().getPortfolioId().equals(portfolioId))
                .filter(a -> !a.isWishlist())
                .toList();

        return buildPerformance(group, holdingAssets);
    }

    /**
     * GET /portfolios/{portfolioId}/asset-groups/performance
     *
     * Performance for every group, scoped to one portfolio. Only groups that actually
     * contain at least one holding asset in that portfolio are returned.
     */
    public List<AssetGroupPerformanceResponse> getAllGroupPerformanceForPortfolio(Long portfolioId) {
        return assetGroupRepository.findAllByOrderByGroupName().stream()
                .map(group -> {
                    List<Asset> holdingAssets = group.getAssets().stream()
                            .filter(a -> a.getPortfolio().getPortfolioId().equals(portfolioId))
                            .filter(a -> !a.isWishlist())
                            .toList();
                    return buildPerformance(group, holdingAssets);
                })
                .filter(p -> p.getHoldingCount() > 0)
                .toList();
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    public AssetGroup findGroupById(Long groupId) {
        return assetGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset group not found with ID: " + groupId));
    }

    private Set<AssetGroup> resolveGroups(List<Long> groupIds) {
        Set<AssetGroup> resolved = new HashSet<>();
        for (Long id : groupIds) {
            resolved.add(findGroupById(id));
        }
        return resolved;
    }

    private AssetGroupPerformanceResponse buildPerformance(AssetGroup group, List<Asset> holdingAssets) {
        BigDecimal totalInvested = holdingAssets.stream()
                .map(Asset::getInvestedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentValue = holdingAssets.stream()
                .map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);
        BigDecimal percentageReturn = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            percentageReturn = absoluteReturn
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return AssetGroupPerformanceResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .holdingCount(holdingAssets.size())
                .totalInvested(totalInvested)
                .currentValue(currentValue)
                .absoluteReturn(absoluteReturn)
                .percentageReturn(percentageReturn)
                .build();
    }

    private AssetGroupResponse mapToResponse(AssetGroup group) {
        List<AssetResponse> assetResponses = group.getAssets().stream()
                .map(assetService::mapToResponse)
                .sorted((a, b) -> a.getAssetName().compareTo(b.getAssetName()))
                .toList();

        return AssetGroupResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .createdDate(group.getCreatedDate())
                .assetCount(group.getAssets().size())
                .assets(assetResponses)
                .build();
    }
}
