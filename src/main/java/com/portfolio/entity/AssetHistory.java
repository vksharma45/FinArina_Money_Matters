package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Append-only audit log for every meaningful change to an asset.
 * Never deleted automatically — used for audit and future analytics.
 */
@Entity
@Table(name = "asset_history",
       indexes = {
           @Index(name = "idx_history_asset_date", columnList = "asset_id, action_date DESC")
       }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private ActionType actionType;

    /** How much quantity changed (positive for buy, negative for sell). */
    @Column(name = "quantity_changed", precision = 15, scale = 4)
    private BigDecimal quantityChanged;

    /** The price at the moment this action was recorded. */
    @Column(name = "price_at_that_time", precision = 15, scale = 2)
    private BigDecimal priceAtThatTime;

    @Column(name = "action_date", nullable = false)
    private LocalDate actionDate;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (actionDate == null) {
            actionDate = LocalDate.now();
        }
    }
}
