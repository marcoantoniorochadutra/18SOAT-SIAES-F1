package com.fiap.siaes.inventory.infrastructure.persistence.mapper;

import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovement;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovementJpaEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Stock Movement")
class StockMovementMapperTest {

    @Test
    @DisplayName("Deve mapear StockMovement para StockMovementJpaEntity")
    void shouldMapStockMovementToStockMovementJpaEntity() {
        StockMovement stockMovement = createStockMovement();

        StockMovementJpaEntity actualEntity = StockMovementMapper.toEntity(stockMovement);

        assertEquals(stockMovement.getId(), actualEntity.getId());
        assertEquals(stockMovement.getSuppliesId(), actualEntity.getSuppliesId());
        assertEquals(stockMovement.getType(), actualEntity.getType());
        assertEquals(stockMovement.getQuantity(), actualEntity.getQuantity());
        assertEquals(stockMovement.getStockAfter(), actualEntity.getStockAfter());
        assertEquals(stockMovement.getReservedAfter(), actualEntity.getReservedAfter());
        assertEquals(stockMovement.getReason(), actualEntity.getReason());
        assertEquals(stockMovement.getReferenceId(), actualEntity.getReferenceId());
        assertEquals(stockMovement.getPerformedBy(), actualEntity.getPerformedBy());
        assertEquals(stockMovement.getPerformedByName(), actualEntity.getPerformedByName());
        assertEquals(stockMovement.getOccurredAt(), actualEntity.getOccurredAt());
    }

    @Test
    @DisplayName("Deve mapear StockMovementJpaEntity para StockMovement")
    void shouldMapStockMovementJpaEntityToStockMovement() {
        StockMovementJpaEntity entity = createStockMovementJpaEntity();

        StockMovement actualStockMovement = StockMovementMapper.toDomain(entity);

        assertEquals(entity.getId(), actualStockMovement.getId());
        assertEquals(entity.getSuppliesId(), actualStockMovement.getSuppliesId());
        assertEquals(entity.getType(), actualStockMovement.getType());
        assertEquals(entity.getQuantity(), actualStockMovement.getQuantity());
        assertEquals(entity.getStockAfter(), actualStockMovement.getStockAfter());
        assertEquals(entity.getReservedAfter(), actualStockMovement.getReservedAfter());
        assertEquals(entity.getReason(), actualStockMovement.getReason());
        assertEquals(entity.getReferenceId(), actualStockMovement.getReferenceId());
        assertEquals(entity.getPerformedBy(), actualStockMovement.getPerformedBy());
        assertEquals(entity.getPerformedByName(), actualStockMovement.getPerformedByName());
        assertEquals(entity.getOccurredAt(), actualStockMovement.getOccurredAt());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando stockMovement não informado")
    void shouldMapToNullWhenStockMovementIsNull() {
        assertNull(StockMovementMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando stockMovementJpaEntity não informado")
    void shouldMapToNullWhenStockMovementJpaEntityIsNull() {
        assertNull(StockMovementMapper.toDomain(null));
    }
}
