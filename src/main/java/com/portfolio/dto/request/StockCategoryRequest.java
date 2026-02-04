package com.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new stock category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;
}
