package com.fiap.siaes.inventory.application.dto;

import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        StockMovementId id,
        StockMovementType type,
        int quantity,
        int stockAfter,
        int reservedAfter,
        String reason,
        UUID referenceId,
        UserId performedBy,
        Instant occurredAt) {

    public static StockMovementResponse from(StockMovement stockMovement) {
        return new StockMovementResponse(
                stockMovement.getId(),
                stockMovement.getType(),
                stockMovement.getQuantity(),
                stockMovement.getStockAfter(),
                stockMovement.getReservedAfter(),
                stockMovement.getReason(),
                stockMovement.getReferenceId(),
                stockMovement.getPerformedBy(),
                stockMovement.getOccurredAt());
    }
}
