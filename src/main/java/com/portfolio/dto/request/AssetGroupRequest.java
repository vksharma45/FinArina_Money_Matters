package com.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating an asset group.
 * Groups are global (not portfolio-scoped).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetGroupRequest {

    @NotBlank(message = "Group name is required")
    private String groupName;

    private String description;
}
