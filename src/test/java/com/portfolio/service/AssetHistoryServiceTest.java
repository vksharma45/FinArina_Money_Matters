package com.portfolio.service;

import com.portfolio.dto.response.AssetHistoryResponse;
import com.portfolio.entity.ActionType;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetHistory;
import com.portfolio.repository.AssetHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetHistoryServiceTest {

    @Mock
    private AssetHistoryRepository assetHistoryRepository;

    @InjectMocks
    private AssetHistoryService assetHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHistory_returnsMappedResponses() {
        Asset a = new Asset();
        a.setAssetId(5L);

        AssetHistory h = new AssetHistory();
        h.setHistoryId(1L);
        h.setAsset(a);
        h.setActionType(ActionType.BUY);
        h.setQuantityChanged(new BigDecimal("2"));
        h.setPriceAtThatTime(new BigDecimal("10"));

        when(assetHistoryRepository.findByAssetAssetIdOrderByActionDateDescHistoryIdDesc(5L))
                .thenReturn(List.of(h));

        List<AssetHistoryResponse> resp = assetHistoryService.getHistory(5L);
        assertEquals(1, resp.size());
        assertEquals(1L, resp.get(0).getHistoryId());
        assertEquals(ActionType.BUY, resp.get(0).getActionType());
    }

    @Test
    void recordMethods_callRepositorySave() {
        Asset a = new Asset();
        a.setAssetId(10L);

        when(assetHistoryRepository.save(any(AssetHistory.class))).thenReturn(new AssetHistory());

        assetHistoryService.recordBuy(a, new BigDecimal("1"), new BigDecimal("10"), "remark");
        assetHistoryService.recordSell(a, new BigDecimal("1"), new BigDecimal("12"), "remark");
        assetHistoryService.recordPriceUpdate(a, new BigDecimal("10"), new BigDecimal("11"));
        assetHistoryService.recordQuantityUpdate(a, new BigDecimal("1"), new BigDecimal("2"));

        verify(assetHistoryRepository, times(4)).save(any(AssetHistory.class));
    }
}
