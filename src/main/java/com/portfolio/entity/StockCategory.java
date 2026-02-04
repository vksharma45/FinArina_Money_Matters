package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * StockCategory entity for grouping similar stocks together.
 * Examples: Technology, Banking, Pharma, FMCG, etc.
 * Performance metrics are calculated at category level, not individual stocks.
 */
@Entity
@Table(name = "stock_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "stockCategory", cascade = CascadeType.ALL)
    private List<Asset> assets = new ArrayList<>();
}
