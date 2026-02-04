package com.portfolio.controller;

import com.portfolio.dto.request.AssetRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
}
