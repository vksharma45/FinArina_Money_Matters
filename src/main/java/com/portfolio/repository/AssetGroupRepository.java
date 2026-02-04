package com.portfolio.repository;

import com.portfolio.entity.AssetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetGroupRepository extends JpaRepository<AssetGroup, Long> {

    /** All groups, sorted by name. */
    List<AssetGroup> findAllByOrderByGroupName();

    /** Name uniqueness check before save (gives readable error vs raw constraint). */
    boolean existsByGroupName(String groupName);
}
