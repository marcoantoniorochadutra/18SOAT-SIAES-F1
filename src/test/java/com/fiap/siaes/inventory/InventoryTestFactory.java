package com.fiap.siaes.inventory;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class InventoryTestFactory {

    public static final int QUANTITY = 10;
    public static final int RESERVED_QUANTITY = 4;
    public static final Long VERSION = 1L;
    public static final String REASON = "Contagem de inventário";
    public static final String PERFORMED_BY_NAME = "João da Silva";

    public static StockItem createStockItem() {
        return createStockItem(QUANTITY, RESERVED_QUANTITY);
    }

    public static StockItem createStockItem(int quantity, int reservedQuantity) {
        return StockItem.recreate()
                .suppliesId(SuppliesId.generate())
                .quantity(quantity)
                .reservedQuantity(reservedQuantity)
                .version(VERSION)
                .build();
    }

    public static StockItem createStockItem(SuppliesId suppliesId, int quantity) {
        return createStockItem(suppliesId, quantity, 0);
    }

    public static StockItem createStockItem(SuppliesId suppliesId, int quantity, int reservedQuantity) {
        return StockItem.recreate()
                .suppliesId(suppliesId)
                .quantity(quantity)
                .reservedQuantity(reservedQuantity)
                .build();
    }

    public static StockItemJpaEntity createStockItemJpaEntity() {
        return StockItemJpaEntity.builder()
                .suppliesId(SuppliesId.generate())
                .quantity(QUANTITY)
                .reservedQuantity(RESERVED_QUANTITY)
                .version(VERSION)
                .build();
    }

    public static StockMovedEvent createStockMovedEvent() {
        return StockMovedEvent.builder()
                .suppliesId(SuppliesId.generate())
                .type(StockMovementType.ADJUSTMENT)
                .quantity(-2)
                .stockAfter(QUANTITY)
                .reservedAfter(RESERVED_QUANTITY)
                .reason(REASON)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .performedByName(PERFORMED_BY_NAME)
                .occurredAt(Instant.now())
                .build();
    }

    public static StockMovement createStockMovement() {
        return StockMovement.recreate()
                .id(StockMovementId.generate())
                .suppliesId(SuppliesId.generate())
                .type(StockMovementType.RESERVATION)
                .quantity(RESERVED_QUANTITY)
                .stockAfter(QUANTITY)
                .reservedAfter(RESERVED_QUANTITY)
                .reason(REASON)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .performedByName(PERFORMED_BY_NAME)
                .occurredAt(Instant.now())
                .build();
    }

    public static StockMovementJpaEntity createStockMovementJpaEntity() {
        return StockMovementJpaEntity.builder()
                .id(StockMovementId.generate())
                .suppliesId(SuppliesId.generate())
                .type(StockMovementType.CONSUMPTION)
                .quantity(RESERVED_QUANTITY)
                .stockAfter(QUANTITY)
                .reservedAfter(0)
                .reason(REASON)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .performedByName(PERFORMED_BY_NAME)
                .occurredAt(Instant.now())
                .build();
    }

}
