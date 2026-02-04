package com.portfolio.controller;

import com.portfolio.dto.request.PortfolioRequest;
import com.portfolio.dto.response.PortfolioResponse;
import com.portfolio.service.AssetGroupService;
import com.portfolio.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private AssetGroupService assetGroupService;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPortfolio_returnsCreatedResponse() {
        PortfolioRequest req = new PortfolioRequest();
        req.setPortfolioName("My Portfolio");
        req.setInitialInvestment(new BigDecimal("5000"));

        PortfolioResponse resp = PortfolioResponse.builder()
                .portfolioId(1L)
                .portfolioName("My Portfolio")
                .createdDate(LocalDate.now())
                .initialInvestment(req.getInitialInvestment())
                .build();

        when(portfolioService.createPortfolio(any(PortfolioRequest.class))).thenReturn(resp);

        ResponseEntity<?> response = portfolioController.createPortfolio(req);
        assertEquals(201, response.getStatusCodeValue());
    }
}
