package com.portfolio.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for adding or replacing an asset's group membership.
 * POST /assets/{id}/groups  → adds these group IDs to the asset
 * PUT  /assets/{id}/groups  → replaces the asset's groups with exactly these group IDs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetGroupMemberRequest {

    @NotEmpty(message = "At least one group ID is required")
    private List<Long> groupIds;
}
