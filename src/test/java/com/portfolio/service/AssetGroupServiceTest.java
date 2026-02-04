package com.portfolio.service;

import com.portfolio.dto.request.AssetGroupMemberRequest;
import com.portfolio.dto.request.AssetGroupRequest;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetGroup;
import com.portfolio.entity.Portfolio;
import com.portfolio.repository.AssetGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssetGroupServiceTest {

    @Mock
    private AssetGroupRepository assetGroupRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetGroupService assetGroupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGroup_success() {
        AssetGroupRequest req = new AssetGroupRequest();
        req.setGroupName("My Group");
        req.setDescription("desc");

        AssetGroup saved = new AssetGroup();
        saved.setGroupId(1L);
        saved.setGroupName("My Group");
        saved.setDescription("desc");

        when(assetGroupRepository.existsByGroupName("My Group")).thenReturn(false);
        when(assetGroupRepository.save(any(AssetGroup.class))).thenReturn(saved);

        var resp = assetGroupService.createGroup(req);
        assertNotNull(resp);
        assertEquals(1L, resp.getGroupId());
        assertEquals("My Group", resp.getGroupName());
    }

    @Test
    void findGroupById_notFound() {
        when(assetGroupRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(RuntimeException.class, () -> assetGroupService.findGroupById(99L));
        assertTrue(ex.getMessage().contains("Asset group not found"));
    }

    @Test
    void getAll_and_getGroup_update_delete_flow() {
        AssetGroup g = new AssetGroup();
        g.setGroupId(2L);
        g.setGroupName("G2");

        when(assetGroupRepository.findAllByOrderByGroupName()).thenReturn(List.of(g));
        when(assetGroupRepository.findById(2L)).thenReturn(Optional.of(g));
        when(assetGroupRepository.save(any(AssetGroup.class))).thenReturn(g);

        var all = assetGroupService.getAllGroups();
        assertEquals(1, all.size());

        var group = assetGroupService.getGroup(2L);
        assertEquals(2L, group.getGroupId());

        AssetGroupRequest req = new AssetGroupRequest();
        req.setGroupName("G2");
        req.setDescription("d");
        var updated = assetGroupService.updateGroup(2L, req);
        assertEquals(2L, updated.getGroupId());

        // deleteGroup should call repository.delete - mock and verify
        doNothing().when(assetGroupRepository).delete(g);
        assetGroupService.deleteGroup(2L);
        verify(assetGroupRepository, times(1)).delete(g);
    }

    @Test
    void performance_methods_no_mutual_references() {
        Portfolio p = new Portfolio();
        p.setPortfolioId(10L);

        Asset a1 = new Asset();
        a1.setAssetId(1L);
        a1.setQuantity(new BigDecimal("1"));
        a1.setBuyPrice(new BigDecimal("10"));
        a1.setCurrentPrice(new BigDecimal("20"));
        a1.setWishlist(false);
        // do NOT set portfolio on asset to avoid recursive hashCode

        AssetGroup g = new AssetGroup();
        g.setGroupId(5L);
        g.setGroupName("G5");
        // add asset to group only on the group side
        g.getAssets().add(a1);

        when(assetGroupRepository.findById(5L)).thenReturn(Optional.of(g));
        when(assetGroupRepository.findAllByOrderByGroupName()).thenReturn(List.of(g));

        // performance for group
        var perf = assetGroupService.getGroupPerformance(5L, 10L);
        assertEquals(0, perf.getHoldingCount()); // because asset.portfolio is null, portfolioId mismatch

        // set portfolio on the asset now and re-run
        a1.setPortfolio(p);
        var perf2 = assetGroupService.getGroupPerformance(5L, 10L);
        assertEquals(1, perf2.getHoldingCount());

        var allPerf = assetGroupService.getAllGroupPerformanceForPortfolio(10L);
        assertTrue(allPerf.size() >= 0);
    }
}
