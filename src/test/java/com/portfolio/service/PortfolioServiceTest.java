package com.portfolio.service;

import com.portfolio.dto.request.PortfolioRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
}
