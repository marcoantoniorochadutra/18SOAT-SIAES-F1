package com.fiap.siaes.inventory.domain.event;

import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record StockMovedEvent(
        SuppliesId suppliesId,
        StockMovementType type,
        int quantity,
        int stockAfter,
        int reservedAfter,
        String reason,
        UUID referenceId,
        UserId performedBy,
        String performedByName,
        Instant occurredAt) implements DomainEvent {
}
