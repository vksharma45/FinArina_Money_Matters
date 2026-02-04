package com.portfolio.service;

import com.portfolio.dto.response.AssetHistoryResponse;
import com.portfolio.entity.ActionType;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetHistory;
import com.portfolio.repository.AssetHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for append-only asset history records.
 * Other services call the record* methods; consumers call getHistory().
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AssetHistoryService {

    private final AssetHistoryRepository assetHistoryRepository;

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    public List<AssetHistoryResponse> getHistory(Long assetId) {
        return assetHistoryRepository
                .findByAssetAssetIdOrderByActionDateDescHistoryIdDesc(assetId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ---------------------------------------------------------------
    // RECORD (called by AssetService, not by controllers directly)
    // ---------------------------------------------------------------

    @Transactional
    public void recordBuy(Asset asset, BigDecimal quantity, BigDecimal price, String remarks) {
        save(asset, ActionType.BUY, quantity, price, remarks);
    }

    @Transactional
    public void recordSell(Asset asset, BigDecimal quantity, BigDecimal price, String remarks) {
        save(asset, ActionType.SELL, quantity.negate(), price, remarks);
    }

    @Transactional
    public void recordPriceUpdate(Asset asset, BigDecimal oldPrice, BigDecimal newPrice) {
        String remarks = "Price changed from " + oldPrice + " to " + newPrice;
        save(asset, ActionType.PRICE_UPDATE, null, newPrice, remarks);
    }

    @Transactional
    public void recordQuantityUpdate(Asset asset, BigDecimal oldQty, BigDecimal newQty) {
        BigDecimal delta = newQty.subtract(oldQty);
        String remarks = "Quantity changed from " + oldQty + " to " + newQty;
        save(asset, ActionType.QUANTITY_UPDATE, delta, asset.getCurrentPrice(), remarks);
    }

    // ---------------------------------------------------------------
    // INTERNAL
    // ---------------------------------------------------------------

    private void save(Asset asset, ActionType actionType, BigDecimal quantityChanged,
                      BigDecimal price, String remarks) {
        AssetHistory history = new AssetHistory();
        history.setAsset(asset);
        history.setActionType(actionType);
        history.setQuantityChanged(quantityChanged);
        history.setPriceAtThatTime(price);
        history.setActionDate(LocalDate.now());
        history.setRemarks(remarks);
        assetHistoryRepository.save(history);
        log.info("History recorded: {} for asset {}", actionType, asset.getAssetId());
    }

    private AssetHistoryResponse mapToResponse(AssetHistory h) {
        return AssetHistoryResponse.builder()
                .historyId(h.getHistoryId())
                .assetId(h.getAsset().getAssetId())
                .actionType(h.getActionType())
                .quantityChanged(h.getQuantityChanged())
                .priceAtThatTime(h.getPriceAtThatTime())
                .actionDate(h.getActionDate())
                .remarks(h.getRemarks())
                .build();
    }
}
