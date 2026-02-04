package com.portfolio.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntitySmokeTest {

    @Test
    void portfolioEntity_gettersSetters() {
        Portfolio p = new Portfolio();
        p.setPortfolioId(10L);
        p.setPortfolioName("P");
        p.setInitialInvestment(new BigDecimal("100"));

        assertEquals(10L, p.getPortfolioId());
        assertEquals("P", p.getPortfolioName());
    }
}
