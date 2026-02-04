package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetGroupResponse {

    private Long groupId;
    private String groupName;
    private String description;
    private LocalDate createdDate;
    private Integer assetCount;
    private List<AssetResponse> assets;
}
