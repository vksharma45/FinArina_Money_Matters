package com.portfolio.service;

import com.portfolio.dto.request.PortfolioRequest;
import com.portfolio.dto.response.PortfolioResponse;
import com.portfolio.dto.response.PortfolioSummaryResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import com.portfolio.exception.ResourceAlreadyExistsException;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    @Transactional
    public PortfolioResponse createPortfolio(PortfolioRequest request) {
        if (portfolioRepository.existsByPortfolioName(request.getPortfolioName())) {
            throw new ResourceAlreadyExistsException(
                    "Portfolio '" + request.getPortfolioName() + "' already exists.");
        }
        Portfolio p = new Portfolio();
        p.setPortfolioName(request.getPortfolioName());
        p.setInitialInvestment(request.getInitialInvestment());
        p.setCreatedDate(LocalDate.now());
        Portfolio saved = portfolioRepository.save(p);
        log.info("Portfolio created: {}", saved.getPortfolioId());
        return mapToResponse(saved);
    }

    public PortfolioResponse getPortfolio(Long portfolioId) {
        return mapToResponse(findPortfolioById(portfolioId));
    }

    public List<PortfolioResponse> getAllPortfolios() {
        return portfolioRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    /**
     * DELETE /portfolios/{portfolioId}
     * Cascade on Portfolio entity handles assets, credit cards.
     * AssetGroup membership rows (join table) are cleared first via asset deletion cascade.
     * AssetHistory rows are NOT deleted (append-only).
     */
    @Transactional
    public void deletePortfolio(Long portfolioId) {
        Portfolio portfolio = findPortfolioById(portfolioId);

        // detach all assets from their groups before cascade-deleting assets
        for (Asset asset : portfolio.getAssets()) {
            asset.getGroups().forEach(g -> g.getAssets().remove(asset));
            asset.getGroups().clear();
        }

        portfolioRepository.delete(portfolio);
        log.info("Portfolio {} deleted", portfolioId);
    }

    // ---------------------------------------------------------------
    // SUMMARY  (holding assets only)
    // ---------------------------------------------------------------

    public PortfolioSummaryResponse getPortfolioSummary(Long portfolioId) {
        Portfolio portfolio = findPortfolioById(portfolioId);

        // only holding assets participate in value calculations
        List<Asset> holdingAssets = assetRepository
                .findByPortfolioPortfolioIdAndWishlistFalse(portfolioId);

        BigDecimal totalInvested = holdingAssets.stream()
                .map(Asset::getInvestedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentValue = holdingAssets.stream()
                .map(Asset::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);
        BigDecimal percentageReturn = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            percentageReturn = absoluteReturn
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        Map<String, BigDecimal> allocation = calculateAssetAllocation(holdingAssets, currentValue);

        return PortfolioSummaryResponse.builder()
                .portfolioId(portfolio.getPortfolioId())
                .portfolioName(portfolio.getPortfolioName())
                .totalInvestedAmount(totalInvested)
                .currentPortfolioValue(currentValue)
                .absoluteReturn(absoluteReturn)
                .percentageReturn(percentageReturn)
                .assetAllocation(allocation)
                .build();
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    public Portfolio findPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found with ID: " + portfolioId));
    }

    private Map<String, BigDecimal> calculateAssetAllocation(List<Asset> holdingAssets, BigDecimal totalValue) {
        Map<String, BigDecimal> allocation = new HashMap<>();
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) return allocation;

        Map<AssetType, BigDecimal> typeValues = new HashMap<>();
        for (Asset a : holdingAssets) {
            typeValues.merge(a.getAssetType(), a.getCurrentValue(), BigDecimal::add);
        }
        for (var entry : typeValues.entrySet()) {
            BigDecimal pct = entry.getValue()
                    .divide(totalValue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            allocation.put(entry.getKey().name(), pct);
        }
        return allocation;
    }

    private PortfolioResponse mapToResponse(Portfolio p) {
        return PortfolioResponse.builder()
                .portfolioId(p.getPortfolioId())
                .portfolioName(p.getPortfolioName())
                .createdDate(p.getCreatedDate())
                .initialInvestment(p.getInitialInvestment())
                .build();
    }
}
