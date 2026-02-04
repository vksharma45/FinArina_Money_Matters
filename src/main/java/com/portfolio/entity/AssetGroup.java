package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A user-defined grouping of assets.
 *
 * Groups are GLOBAL — not scoped to a single portfolio.
 * The same group can contain assets from multiple portfolios via the join table.
 * Group name is unique across the entire system.
 *
 * Performance calculations for a group are always scoped by portfolioId at query time.
 */
@Entity
@Table(name = "asset_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", nullable = false, unique = true, length = 100)
    private String groupName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    /**
     * Owning side of the Many-to-Many. The join table lives here.
     */
    @ManyToMany
    @JoinTable(
            name = "asset_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private Set<Asset> assets = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public void removeAsset(Asset asset) {
        assets.remove(asset);
    }
}
