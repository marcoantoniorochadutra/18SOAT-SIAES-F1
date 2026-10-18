package com.fiap.siaes.supplies.infrastructure.persistence.mapper;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSuppliesJpaEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Supplies")
class SuppliesMapperTest {

    @Test
    @DisplayName("Deve mapear Supplies para SuppliesJpaEntity")
    void shouldMapSuppliesToSuppliesJpaEntity() {
        Supplies supplies = createSupplies(false);

        SuppliesJpaEntity actualEntity = SuppliesMapper.toEntity(supplies);

        assertEquals(supplies.getId(), actualEntity.getId());
        assertEquals(supplies.getSku().getValue(), actualEntity.getSku());
        assertEquals(supplies.getDescription(), actualEntity.getDescription());
        assertEquals(supplies.getUnitPrice(), actualEntity.getUnitPrice());
        assertEquals(supplies.isActive(), actualEntity.isActive());
    }

    @Test
    @DisplayName("Deve mapear SuppliesJpaEntity para Supplies")
    void shouldMapSuppliesJpaEntityToSupplies() {
        SuppliesJpaEntity entity = createSuppliesJpaEntity();

        Supplies actualSupplies = SuppliesMapper.toDomain(entity);

        assertEquals(entity.getId(), actualSupplies.getId());
        assertEquals(entity.getSku(), actualSupplies.getSku().getValue());
        assertEquals(entity.getDescription(), actualSupplies.getDescription());
        assertEquals(entity.getUnitPrice(), actualSupplies.getUnitPrice());
        assertEquals(entity.isActive(), actualSupplies.isActive());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando supplies não informado")
    void shouldMapToNullWhenSuppliesIsNull() {
        assertNull(SuppliesMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando suppliesJpaEntity não informado")
    void shouldMapToNullWhenSuppliesJpaEntityIsNull() {
        assertNull(SuppliesMapper.toDomain(null));
    }
}
