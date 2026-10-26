package com.fiap.siaes.inventory.application.dto;

import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StockMovementResponse(
        StockMovementId id,
        StockMovementType type,
        int quantity,
        int stockAfter,
        int reservedAfter,
        String reason,
        UUID referenceId,
        UserId performedBy,
        String performedByName,
        Instant occurredAt) {

    public static StockMovementResponse from(StockMovement stockMovement) {
        return StockMovementResponse.builder()
                .id(stockMovement.getId())
                .type(stockMovement.getType())
                .quantity(stockMovement.getQuantity())
                .stockAfter(stockMovement.getStockAfter())
                .reservedAfter(stockMovement.getReservedAfter())
                .reason(stockMovement.getReason())
                .referenceId(stockMovement.getReferenceId())
                .performedBy(stockMovement.getPerformedBy())
                .performedByName(stockMovement.getPerformedByName())
                .occurredAt(stockMovement.getOccurredAt())
                .build();
    }
}
