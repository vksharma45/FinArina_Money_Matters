package com.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for stock category — replaces raw entity exposure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private String description;
}
