package com.portfolio.service;

import com.portfolio.dto.request.PortfolioRequest;
import com.portfolio.dto.response.PortfolioSummaryResponse;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPortfolio_success() {
        PortfolioRequest req = new PortfolioRequest();
        req.setPortfolioName("Test Portfolio");
        req.setInitialInvestment(new BigDecimal("1000"));

        Portfolio saved = new Portfolio();
        saved.setPortfolioId(1L);
        saved.setPortfolioName(req.getPortfolioName());
        saved.setInitialInvestment(req.getInitialInvestment());
        saved.setCreatedDate(LocalDate.now());

        when(portfolioRepository.existsByPortfolioName("Test Portfolio")).thenReturn(false);
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(saved);

        var resp = portfolioService.createPortfolio(req);

        assertNotNull(resp);
        assertEquals(saved.getPortfolioId(), resp.getPortfolioId());
        assertEquals("Test Portfolio", resp.getPortfolioName());
    }

    @Test
    void findPortfolioById_notFound() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(RuntimeException.class, () -> portfolioService.findPortfolioById(99L));
        assertTrue(ex.getMessage().contains("Portfolio not found"));
    }

    @Test
    void getPortfolio_and_getAllPortfolios() {
        Portfolio p = new Portfolio();
        p.setPortfolioId(2L);
        p.setPortfolioName("P2");
        p.setCreatedDate(LocalDate.now());
        p.setInitialInvestment(new BigDecimal("500"));

        when(portfolioRepository.findById(2L)).thenReturn(Optional.of(p));
        when(portfolioRepository.findAll()).thenReturn(List.of(p));

        var single = portfolioService.getPortfolio(2L);
        assertEquals(2L, single.getPortfolioId());

        var all = portfolioService.getAllPortfolios();
        assertEquals(1, all.size());
        assertEquals("P2", all.get(0).getPortfolioName());
    }

    @Test
    void deletePortfolio_callsRepositoryDelete() {
        Portfolio p = new Portfolio();
        p.setPortfolioId(3L);
        p.setAssets(List.of()); // no-op

        when(portfolioRepository.findById(3L)).thenReturn(Optional.of(p));
        doNothing().when(portfolioRepository).delete(p);

        portfolioService.deletePortfolio(3L);

        verify(portfolioRepository, times(1)).delete(p);
    }

    @Test
    void getPortfolioSummary_calculations() {
        Portfolio p = new Portfolio();
        p.setPortfolioId(4L);
        p.setPortfolioName("SummaryP");

        Asset a1 = new Asset();
        a1.setAssetType(AssetType.STOCK);
        a1.setQuantity(new BigDecimal("2"));
        a1.setBuyPrice(new BigDecimal("10"));
        a1.setCurrentPrice(new BigDecimal("15"));
        a1.setWishlist(false);
        a1.setPortfolio(p);

        Asset a2 = new Asset();
        a2.setAssetType(AssetType.CASH);
        a2.setQuantity(new BigDecimal("1"));
        a2.setBuyPrice(new BigDecimal("100"));
        a2.setCurrentPrice(new BigDecimal("120"));
        a2.setWishlist(false);
        a2.setPortfolio(p);

        when(portfolioRepository.findById(4L)).thenReturn(Optional.of(p));
        when(assetRepository.findByPortfolioPortfolioIdAndWishlistFalse(4L)).thenReturn(List.of(a1, a2));

        PortfolioSummaryResponse summary = portfolioService.getPortfolioSummary(4L);
        assertNotNull(summary);
        // invested = 2*10 + 1*100 = 120
        assertEquals(new BigDecimal("120"), summary.getTotalInvestedAmount());
        // current = 2*15 + 1*120 = 150
        assertEquals(new BigDecimal("150"), summary.getCurrentPortfolioValue());
        // absolute = 30
        assertEquals(new BigDecimal("30"), summary.getAbsoluteReturn());
        assertTrue(summary.getPercentageReturn().compareTo(new BigDecimal("25")) == 0 || summary.getPercentageReturn().compareTo(new BigDecimal("25.0000"))==0);
        assertTrue(summary.getAssetAllocation().size() > 0);
    }
}
