package com.portfolio.controller;

import com.portfolio.dto.request.AssetBuyRequest;
import com.portfolio.dto.request.AssetGroupMemberRequest;
import com.portfolio.dto.request.AssetRequest;
import com.portfolio.dto.request.AssetUpdateRequest;
import com.portfolio.dto.response.AssetGroupResponse;
import com.portfolio.dto.response.AssetHistoryResponse;
import com.portfolio.dto.response.AssetPerformanceResponse;
import com.portfolio.dto.response.AssetResponse;
import com.portfolio.service.AssetGroupService;
import com.portfolio.service.AssetHistoryService;
import com.portfolio.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @Mock
    private AssetHistoryService assetHistoryService;

    @Mock
    private AssetGroupService assetGroupService;

    @InjectMocks
    private AssetController assetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAsset_returnsCreated() {
        AssetRequest req = new AssetRequest();
        req.setAssetName("Test Asset");
        req.setIsWishlist(true);

        AssetResponse resp = AssetResponse.builder()
                .assetId(1L)
                .assetName("Test Asset")
                .build();

        when(assetService.createAsset(any(Long.class), any(AssetRequest.class))).thenReturn(resp);

        ResponseEntity<?> response = assetController.createAsset(1L, req);
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void getPortfolioAssets_returnsOk() {
        when(assetService.getPortfolioAssets(1L)).thenReturn(List.of(AssetResponse.builder().assetId(1L).build()));
        ResponseEntity<?> resp = assetController.getPortfolioAssets(1L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void getWishlist_returnsOk() {
        when(assetService.getWishlistAssets(2L)).thenReturn(List.of());
        ResponseEntity<?> resp = assetController.getWishlist(2L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void getAsset_returnsOk() {
        when(assetService.getAsset(5L)).thenReturn(AssetResponse.builder().assetId(5L).build());
        ResponseEntity<?> resp = assetController.getAsset(5L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void updateAsset_returnsOk() {
        AssetUpdateRequest req = new AssetUpdateRequest();
        when(assetService.updateAsset(eq(3L), any(AssetUpdateRequest.class)))
                .thenReturn(AssetResponse.builder().assetId(3L).build());
        ResponseEntity<?> resp = assetController.updateAsset(3L, req);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void deleteAsset_returnsOk() {
        doNothing().when(assetService).deleteAsset(4L);
        ResponseEntity<?> resp = assetController.deleteAsset(4L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void buyAsset_returnsOk() {
        AssetBuyRequest req = new AssetBuyRequest();
        req.setBuyPrice(new BigDecimal("100"));
        when(assetService.buyAsset(eq(7L), any(AssetBuyRequest.class)))
                .thenReturn(AssetResponse.builder().assetId(7L).build());
        ResponseEntity<?> resp = assetController.buyAsset(7L, req);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void getAssetHistory_returnsOk() {
        when(assetHistoryService.getHistory(8L)).thenReturn(List.of(AssetHistoryResponse.builder().historyId(1L).build()));
        // assetService.findAssetById will be called to validate existence; mock it
        when(assetService.findAssetById(8L)).thenReturn(null);
        ResponseEntity<?> resp = assetController.getAssetHistory(8L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void getAssetPerformance_returnsOk() {
        when(assetService.getPerformance(9L)).thenReturn(AssetPerformanceResponse.builder().assetId(9L).build());
        ResponseEntity<?> resp = assetController.getAssetPerformance(9L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void addReplaceRemoveGetGroups_endpoints() {
        AssetGroupMemberRequest req = new AssetGroupMemberRequest();
        req.setGroupIds(List.of(1L, 2L));

        when(assetGroupService.addGroupsToAsset(eq(10L), any(AssetGroupMemberRequest.class)))
                .thenReturn(List.of(AssetGroupResponse.builder().groupId(1L).build()));
        ResponseEntity<?> addResp = assetController.addGroups(10L, req);
        assertEquals(200, addResp.getStatusCodeValue());

        when(assetGroupService.replaceGroupsForAsset(eq(10L), any(AssetGroupMemberRequest.class)))
                .thenReturn(List.of(AssetGroupResponse.builder().groupId(2L).build()));
        ResponseEntity<?> replaceResp = assetController.replaceGroups(10L, req);
        assertEquals(200, replaceResp.getStatusCodeValue());

        doNothing().when(assetGroupService).removeAssetFromGroup(10L, 2L);
        ResponseEntity<?> delResp = assetController.removeGroup(10L, 2L);
        assertEquals(200, delResp.getStatusCodeValue());

        when(assetGroupService.getGroupsForAsset(10L)).thenReturn(List.of(AssetGroupResponse.builder().groupId(1L).build()));
        ResponseEntity<?> getResp = assetController.getGroups(10L);
        assertEquals(200, getResp.getStatusCodeValue());
    }
}
