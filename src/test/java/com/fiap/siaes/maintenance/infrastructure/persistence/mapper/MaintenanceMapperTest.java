package com.fiap.siaes.maintenance.infrastructure.persistence.mapper;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenanceJpaEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Maintenance")
class MaintenanceMapperTest {

    @Test
    @DisplayName("Deve mapear Maintenance para MaintenanceJpaEntity")
    void shouldMapMaintenanceToMaintenanceJpaEntity() {
        Maintenance maintenance = createMaintenance(false);

        MaintenanceJpaEntity actualEntity = MaintenanceMapper.toEntity(maintenance);

        assertEquals(maintenance.getId(), actualEntity.getId());
        assertEquals(maintenance.getCode().getValue(), actualEntity.getCode());
        assertEquals(maintenance.getDescription(), actualEntity.getDescription());
        assertEquals(maintenance.getPrice(), actualEntity.getPrice());
        assertEquals(maintenance.getEstimatedDurationMinutes(), actualEntity.getEstimatedDurationMinutes());
        assertEquals(maintenance.isActive(), actualEntity.isActive());
    }

    @Test
    @DisplayName("Deve mapear MaintenanceJpaEntity para Maintenance")
    void shouldMapMaintenanceJpaEntityToMaintenance() {
        MaintenanceJpaEntity entity = createMaintenanceJpaEntity();

        Maintenance actualMaintenance = MaintenanceMapper.toDomain(entity);

        assertEquals(entity.getId(), actualMaintenance.getId());
        assertEquals(entity.getCode(), actualMaintenance.getCode().getValue());
        assertEquals(entity.getDescription(), actualMaintenance.getDescription());
        assertEquals(entity.getPrice(), actualMaintenance.getPrice());
        assertEquals(entity.getEstimatedDurationMinutes(), actualMaintenance.getEstimatedDurationMinutes());
        assertEquals(entity.isActive(), actualMaintenance.isActive());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando maintenance não informado")
    void shouldMapToNullWhenMaintenanceIsNull() {
        assertNull(MaintenanceMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando maintenanceJpaEntity não informado")
    void shouldMapToNullWhenMaintenanceJpaEntityIsNull() {
        assertNull(MaintenanceMapper.toDomain(null));
    }
}
