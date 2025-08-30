package com.noor.store.model;

/**
 * Types of stock movements.
 */
public enum MovementType {
    PURCHASE, // inbound from supplier
    SALE,     // outbound for customer
    MANUAL_ADD,
    MANUAL_REMOVE,
    ADJUSTMENT
}
