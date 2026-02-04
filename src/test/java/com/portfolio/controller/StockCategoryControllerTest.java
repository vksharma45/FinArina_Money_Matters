package com.portfolio.controller;

import com.portfolio.dto.request.StockCategoryRequest;
import com.portfolio.dto.response.StockCategoryResponse;
import com.portfolio.service.StockCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StockCategoryControllerTest {

    @Mock
    private StockCategoryService stockCategoryService;

    @InjectMocks
    private StockCategoryController stockCategoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_returnsCreated() {
        StockCategoryRequest req = new StockCategoryRequest();
        req.setCategoryName("Tech");

        StockCategoryResponse resp = StockCategoryResponse.builder()
                .categoryId(1L)
                .categoryName("Tech")
                .build();

        when(stockCategoryService.createCategory(any(StockCategoryRequest.class))).thenReturn(resp);

        ResponseEntity<?> response = stockCategoryController.createCategory(req);
        assertEquals(201, response.getStatusCodeValue());
    }
}
