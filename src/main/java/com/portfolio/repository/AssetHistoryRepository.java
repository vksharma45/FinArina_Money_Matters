package com.portfolio.repository;

import com.portfolio.entity.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {

    /** Full history for one asset, newest first. */
    List<AssetHistory> findByAssetAssetIdOrderByActionDateDescHistoryIdDesc(Long assetId);
}
