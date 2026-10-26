package com.fiap.siaes.inventory.domain.model;

import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.REASON;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovedEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("UnitTest - Domain - Stock Movement")
class StockMovementDomainTest {

    @Test
    @DisplayName("Deve criar a movimentação a partir do evento de estoque movimentado")
    void shouldCreateStockMovementFromEvent() {
        var event = createStockMovedEvent();

        StockMovement stockMovement = StockMovement.from(event);

        assertNotNull(stockMovement.getId());
        assertEquals(event.suppliesId(), stockMovement.getSuppliesId());
        assertEquals(event.type(), stockMovement.getType());
        assertEquals(event.quantity(), stockMovement.getQuantity());
        assertEquals(event.stockAfter(), stockMovement.getStockAfter());
        assertEquals(event.reservedAfter(), stockMovement.getReservedAfter());
        assertEquals(event.reason(), stockMovement.getReason());
        assertEquals(event.referenceId(), stockMovement.getReferenceId());
        assertEquals(event.performedBy(), stockMovement.getPerformedBy());
        assertEquals(event.occurredAt(), stockMovement.getOccurredAt());
    }

    @Test
    @DisplayName("Deve gerar um id diferente para cada movimentação criada a partir de eventos")
    void shouldGenerateDifferentIdForEachMovement() {
        var event = createStockMovedEvent();

        assertNotEquals(StockMovement.from(event).getId(), StockMovement.from(event).getId());
    }

    @Test
    @DisplayName("Deve recriar a movimentação mantendo os dados informados")
    void shouldRecreateStockMovementKeepingGivenData() {
        var id = StockMovementId.generate();
        var suppliesId = SuppliesId.generate();
        var referenceId = UUID.randomUUID();
        var performedBy = UserId.generate();
        var occurredAt = Instant.now();

        StockMovement stockMovement = StockMovement.recreate()
                .id(id)
                .suppliesId(suppliesId)
                .type(StockMovementType.ENTRY)
                .quantity(QUANTITY)
                .stockAfter(QUANTITY)
                .reservedAfter(RESERVED_QUANTITY)
                .reason(REASON)
                .referenceId(referenceId)
                .performedBy(performedBy)
                .occurredAt(occurredAt)
                .build();

        assertEquals(id, stockMovement.getId());
        assertEquals(suppliesId, stockMovement.getSuppliesId());
        assertEquals(StockMovementType.ENTRY, stockMovement.getType());
        assertEquals(QUANTITY, stockMovement.getQuantity());
        assertEquals(QUANTITY, stockMovement.getStockAfter());
        assertEquals(RESERVED_QUANTITY, stockMovement.getReservedAfter());
        assertEquals(REASON, stockMovement.getReason());
        assertEquals(referenceId, stockMovement.getReferenceId());
        assertEquals(performedBy, stockMovement.getPerformedBy());
        assertEquals(occurredAt, stockMovement.getOccurredAt());
    }
}
