package com.fiap.siaes.inventory.domain.model;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class StockMovement {

    private final StockMovementId id;
    private final SuppliesId suppliesId;
    private final StockMovementType type;
    private final int quantity;
    private final int stockAfter;
    private final int reservedAfter;
    private final String reason;
    private final UUID referenceId;
    private final UserId performedBy;
    private final String performedByName;
    private final Instant occurredAt;

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public StockMovement(StockMovementId id, SuppliesId suppliesId, StockMovementType type, int quantity,
                         int stockAfter, int reservedAfter, String reason, UUID referenceId,
                         UserId performedBy, String performedByName, Instant occurredAt) {
        this.id = id;
        this.suppliesId = suppliesId;
        this.type = type;
        this.quantity = quantity;
        this.stockAfter = stockAfter;
        this.reservedAfter = reservedAfter;
        this.reason = reason;
        this.referenceId = referenceId;
        this.performedBy = performedBy;
        this.performedByName = performedByName;
        this.occurredAt = occurredAt;
    }

    public static StockMovement from(StockMovedEvent event) {
        return new StockMovement(
                StockMovementId.generate(),
                event.suppliesId(),
                event.type(),
                event.quantity(),
                event.stockAfter(),
                event.reservedAfter(),
                event.reason(),
                event.referenceId(),
                event.performedBy(),
                event.performedByName(),
                event.occurredAt());
    }
}
