package com.portfolio.service;

import com.portfolio.dto.request.CreditCardRequest;
import com.portfolio.dto.response.CreditCardResponse;
import com.portfolio.entity.CreditCard;
import com.portfolio.entity.Portfolio;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final PortfolioService portfolioService;

    // ---------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------

    @Transactional
    public CreditCardResponse addCreditCard(CreditCardRequest request) {
        Portfolio portfolio = portfolioService.findPortfolioById(request.getPortfolioId());

        CreditCard card = new CreditCard();
        card.setPortfolio(portfolio);
        card.setCardName(request.getCardName());
        card.setCreditLimit(request.getCreditLimit());
        card.setOutstandingAmount(request.getOutstandingAmount());
        card.setDueDate(request.getDueDate());

        CreditCard saved = creditCardRepository.save(card);
        log.info("Credit card created: {}", saved.getCardId());
        return mapToResponse(saved);
    }

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    public CreditCardResponse getCreditCard(Long cardId) {
        return mapToResponse(findById(cardId));
    }

    public List<CreditCardResponse> getPortfolioCreditCards(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);
        return creditCardRepository.findByPortfolioPortfolioId(portfolioId)
                .stream().map(this::mapToResponse).toList();
    }

    public List<CreditCardResponse> getUpcomingDueCards(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);
        return creditCardRepository
                .findUpcomingDueCards(portfolioId, LocalDate.now().plusDays(5))
                .stream().map(this::mapToResponse).toList();
    }

    public List<CreditCardResponse> getOverdueCards(Long portfolioId) {
        portfolioService.findPortfolioById(portfolioId);
        return creditCardRepository
                .findOverdueCards(portfolioId, LocalDate.now())
                .stream().map(this::mapToResponse).toList();
    }

    // ---------------------------------------------------------------
    // UPDATE (full — PUT)
    // ---------------------------------------------------------------

    @Transactional
    public CreditCardResponse updateCreditCard(Long cardId, CreditCardRequest request) {
        CreditCard card = findById(cardId);

        if (request.getCardName() != null)          card.setCardName(request.getCardName());
        if (request.getCreditLimit() != null)       card.setCreditLimit(request.getCreditLimit());
        if (request.getOutstandingAmount() != null) card.setOutstandingAmount(request.getOutstandingAmount());
        if (request.getDueDate() != null)           card.setDueDate(request.getDueDate());

        CreditCard updated = creditCardRepository.save(card);
        log.info("Credit card {} updated", cardId);
        return mapToResponse(updated);
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    @Transactional
    public void deleteCreditCard(Long cardId) {
        CreditCard card = findById(cardId);
        creditCardRepository.delete(card);
        log.info("Credit card {} deleted", cardId);
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    private CreditCard findById(Long cardId) {
        return creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with ID: " + cardId));
    }

    private CreditCardResponse mapToResponse(CreditCard card) {
        String dueStatus = card.getDueStatus();
        long daysUntilDue = card.getDaysUntilDue();

        String alertMessage = switch (dueStatus) {
            case "OVERDUE" -> String.format(
                    "⚠️ OVERDUE: '%s' is %d days overdue. Pay immediately.",
                    card.getCardName(), Math.abs(daysUntilDue));
            case "WARNING" -> String.format(
                    "⏰ WARNING: '%s' due in %d day%s. Pay soon.",
                    card.getCardName(), daysUntilDue, daysUntilDue == 1 ? "" : "s");
            default -> String.format(
                    "✓ OK: '%s' due in %d days.",
                    card.getCardName(), daysUntilDue);
        };

        return CreditCardResponse.builder()
                .cardId(card.getCardId())
                .portfolioId(card.getPortfolio().getPortfolioId())
                .cardName(card.getCardName())
                .creditLimit(card.getCreditLimit())
                .outstandingAmount(card.getOutstandingAmount())
                .availableCredit(card.getAvailableCredit())
                .creditUtilization(card.getCreditUtilization())
                .dueDate(card.getDueDate())
                .daysUntilDue(daysUntilDue)
                .dueStatus(dueStatus)
                .alertMessage(alertMessage)
                .build();
    }
}
