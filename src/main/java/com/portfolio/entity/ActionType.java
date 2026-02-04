package com.portfolio.entity;

/**
 * Enum representing the type of action recorded in asset history.
 * Used for audit trail and future analytics.
 */
public enum ActionType {
    BUY,
    SELL,
    PRICE_UPDATE,
    QUANTITY_UPDATE
}
