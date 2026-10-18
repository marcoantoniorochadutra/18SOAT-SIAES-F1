package com.fiap.siaes.inventory.domain.model;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InsufficientStockException;
import com.fiap.siaes.inventory.domain.exception.InvalidStockMovementTypeException;
import com.fiap.siaes.inventory.domain.exception.InvalidStockQuantityException;
import com.fiap.siaes.inventory.domain.exception.ReservedStockExceededException;
import com.fiap.siaes.inventory.domain.exception.StockAdjustmentReasonRequiredException;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.sk.domain.model.AggregateRoot;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class StockItem extends AggregateRoot {

    private final SuppliesId suppliesId;
    private int quantity;
    private int reservedQuantity;
    private Long version;

    @Builder
    public StockItem(SuppliesId suppliesId, int initialQuantity, UserId performedBy) {
        this.suppliesId = suppliesId;
        this.quantity = 0;
        this.reservedQuantity = 0;

        if (initialQuantity < 0) {
            throw new InvalidStockQuantityException(suppliesId.toString(), initialQuantity);
        }

        if (initialQuantity > 0) {
            this.quantity = initialQuantity;
            this.registerMovement(StockMovementType.INITIAL, initialQuantity, null, null, performedBy);
        }
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public StockItem(SuppliesId suppliesId, int quantity, int reservedQuantity, Long version) {
        this.suppliesId = suppliesId;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.version = version;
    }

    public int getAvailableQuantity() {
        return this.quantity - this.reservedQuantity;
    }

    public void moveStock(StockMovementType type, int quantity, String reason, UserId performedBy) {
        switch (type) {
            case ENTRY -> this.increase(quantity);
            case ADJUSTMENT -> this.adjust(quantity, reason);
            default -> throw new InvalidStockMovementTypeException(this.suppliesId.toString(), type.name());
        }

        this.registerMovement(type, quantity, reason, null, performedBy);
    }

    public void reserve(int amount, UUID referenceId, UserId performedBy) {
        this.validatePositive(amount);
        this.validateAvailable(amount);
        this.reservedQuantity += amount;

        this.registerMovement(StockMovementType.RESERVATION, amount, null, referenceId, performedBy);
    }

    public void releaseReservation(int amount, UUID referenceId, UserId performedBy) {
        this.validatePositive(amount);
        this.validateReserved(amount);
        this.reservedQuantity -= amount;

        this.registerMovement(StockMovementType.RESERVATION_RELEASE, amount, null, referenceId, performedBy);
    }

    public void consumeReservation(int amount, UUID referenceId, UserId performedBy) {
        this.validatePositive(amount);
        this.validateReserved(amount);
        this.reservedQuantity -= amount;
        this.quantity -= amount;

        this.registerMovement(StockMovementType.CONSUMPTION, amount, null, referenceId, performedBy);
    }

    private void increase(int amount) {
        this.validatePositive(amount);
        this.quantity += amount;
    }

    private void decrease(int amount) {
        this.validatePositive(amount);
        this.validateAvailable(amount);
        this.quantity -= amount;
    }

    private void adjust(int quantity, String reason) {
        if (isBlank(reason)) {
            throw new StockAdjustmentReasonRequiredException(this.suppliesId.toString());
        }

        if (quantity > 0) {
            this.increase(quantity);
        } else {
            this.decrease(-quantity);
        }
    }

    private void registerMovement(StockMovementType type, int quantity, String reason, UUID referenceId, UserId performedBy) {
        this.registerEvent(StockMovedEvent.builder()
                .suppliesId(this.suppliesId)
                .type(type)
                .quantity(quantity)
                .stockAfter(this.quantity)
                .reservedAfter(this.reservedQuantity)
                .reason(reason)
                .referenceId(referenceId)
                .performedBy(performedBy)
                .occurredAt(Instant.now())
                .build());
    }

    private void validatePositive(int amount) {
        if (amount <= 0) {
            throw new InvalidStockQuantityException(this.suppliesId.toString(), amount);
        }
    }

    private void validateAvailable(int amount) {
        if (amount > this.getAvailableQuantity()) {
            throw new InsufficientStockException(this.suppliesId.toString(), this.getAvailableQuantity(), amount);
        }
    }

    private void validateReserved(int amount) {
        if (amount > this.reservedQuantity) {
            throw new ReservedStockExceededException(this.suppliesId.toString(), this.reservedQuantity, amount);
        }
    }
}
