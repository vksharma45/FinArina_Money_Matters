package com.portfolio.controller;

import com.portfolio.dto.request.CreditCardRequest;
import com.portfolio.dto.response.ApiResponse;
import com.portfolio.dto.response.CreditCardResponse;
import com.portfolio.service.CreditCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Spec routes:
 *   POST   /portfolios/{portfolioId}/credit-cards    — create
 *   GET    /portfolios/{portfolioId}/credit-cards    — list
 *   PUT    /credit-cards/{cardId}                    — update
 *   DELETE /credit-cards/{cardId}                    — delete
 *
 * Extra convenience (kept from previous version):
 *   GET    /credit-cards/{cardId}                    — single card
 *   GET    /portfolios/{portfolioId}/credit-cards/upcoming-due
 *   GET    /portfolios/{portfolioId}/credit-cards/overdue
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CreditCardController {

    private final CreditCardService creditCardService;

    // --- nested under portfolio ---

    @PostMapping("/portfolios/{portfolioId}/credit-cards")
    public ResponseEntity<ApiResponse<CreditCardResponse>> addCreditCard(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CreditCardRequest request) {
        // portfolioId from path is the authoritative one; set it on the request
        request.setPortfolioId(portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Credit card added successfully",
                        creditCardService.addCreditCard(request)));
    }

    @GetMapping("/portfolios/{portfolioId}/credit-cards")
    public ResponseEntity<ApiResponse<List<CreditCardResponse>>> getPortfolioCreditCards(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Credit cards retrieved successfully",
                creditCardService.getPortfolioCreditCards(portfolioId)));
    }

    @GetMapping("/portfolios/{portfolioId}/credit-cards/upcoming-due")
    public ResponseEntity<ApiResponse<List<CreditCardResponse>>> getUpcomingDueCards(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Upcoming due cards retrieved successfully",
                creditCardService.getUpcomingDueCards(portfolioId)));
    }

    @GetMapping("/portfolios/{portfolioId}/credit-cards/overdue")
    public ResponseEntity<ApiResponse<List<CreditCardResponse>>> getOverdueCards(
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success("Overdue cards retrieved successfully",
                creditCardService.getOverdueCards(portfolioId)));
    }

    // --- standalone card routes ---

    @GetMapping("/credit-cards/{cardId}")
    public ResponseEntity<ApiResponse<CreditCardResponse>> getCreditCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(ApiResponse.success("Credit card retrieved successfully",
                creditCardService.getCreditCard(cardId)));
    }

    @PutMapping("/credit-cards/{cardId}")
    public ResponseEntity<ApiResponse<CreditCardResponse>> updateCreditCard(
            @PathVariable Long cardId,
            @Valid @RequestBody CreditCardRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Credit card updated successfully",
                creditCardService.updateCreditCard(cardId, request)));
    }

    @DeleteMapping("/credit-cards/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deleteCreditCard(@PathVariable Long cardId) {
        creditCardService.deleteCreditCard(cardId);
        return ResponseEntity.ok(ApiResponse.success("Credit card deleted successfully", null));
    }
}
