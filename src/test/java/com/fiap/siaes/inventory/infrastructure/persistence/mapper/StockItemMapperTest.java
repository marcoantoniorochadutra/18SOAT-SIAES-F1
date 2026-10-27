package com.fiap.siaes.inventory.infrastructure.persistence.mapper;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItemJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Stock Item")
class StockItemMapperTest {

    @Test
    @DisplayName("Deve mapear StockItem para StockItemJpaEntity")
    void shouldMapStockItemToStockItemJpaEntity() {
        StockItem stockItem = createStockItem();

        StockItemJpaEntity actualEntity = StockItemMapper.toEntity(stockItem);

        assertEquals(stockItem.getSuppliesId(), actualEntity.getSuppliesId());
        assertEquals(stockItem.getQuantity(), actualEntity.getQuantity());
        assertEquals(stockItem.getReservedQuantity(), actualEntity.getReservedQuantity());
        assertEquals(stockItem.getVersion(), actualEntity.getVersion());
    }

    @Test
    @DisplayName("Deve mapear StockItemJpaEntity para StockItem sem eventos pendentes")
    void shouldMapStockItemJpaEntityToStockItem() {
        StockItemJpaEntity entity = createStockItemJpaEntity();

        StockItem actualStockItem = StockItemMapper.toDomain(entity);

        assertEquals(entity.getSuppliesId(), actualStockItem.getSuppliesId());
        assertEquals(entity.getQuantity(), actualStockItem.getQuantity());
        assertEquals(entity.getReservedQuantity(), actualStockItem.getReservedQuantity());
        assertEquals(entity.getVersion(), actualStockItem.getVersion());
        assertThat(actualStockItem.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve mapear para nulo quando stockItem não informado")
    void shouldMapToNullWhenStockItemIsNull() {
        assertNull(StockItemMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando stockItemJpaEntity não informado")
    void shouldMapToNullWhenStockItemJpaEntityIsNull() {
        assertNull(StockItemMapper.toDomain(null));
    }
}
