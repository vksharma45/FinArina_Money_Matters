package com.portfolio.dto.response;

import com.portfolio.entity.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetHistoryResponse {

    private Long historyId;
    private Long assetId;
    private ActionType actionType;
    private BigDecimal quantityChanged;
    private BigDecimal priceAtThatTime;
    private LocalDate actionDate;
    private String remarks;
}
