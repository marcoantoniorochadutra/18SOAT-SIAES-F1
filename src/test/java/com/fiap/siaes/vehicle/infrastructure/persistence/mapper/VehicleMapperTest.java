package com.fiap.siaes.vehicle.infrastructure.persistence.mapper;

import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicleJpaEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Vehicle")
class VehicleMapperTest {

    @Test
    @DisplayName("Deve mapear Vehicle para VehicleJpaEntity")
    void shouldMapVehicleToVehicleJpaEntity() {
        Vehicle vehicle = createVehicle();

        VehicleJpaEntity actualEntity = VehicleMapper.toEntity(vehicle);

        assertEquals(vehicle.getId(), actualEntity.getId());
        assertEquals(vehicle.getCustomerId(), actualEntity.getCustomerId());
        assertEquals(vehicle.getPlate().getValue(), actualEntity.getPlate());
        assertEquals(vehicle.getBrand(), actualEntity.getBrand());
        assertEquals(vehicle.getModel(), actualEntity.getModel());
        assertEquals(vehicle.getYear(), actualEntity.getYear());
    }

    @Test
    @DisplayName("Deve mapear VehicleJpaEntity para Vehicle")
    void shouldMapVehicleJpaEntityToVehicle() {
        VehicleJpaEntity entity = createVehicleJpaEntity();

        Vehicle actualVehicle = VehicleMapper.toDomain(entity);

        assertEquals(entity.getId(), actualVehicle.getId());
        assertEquals(entity.getCustomerId(), actualVehicle.getCustomerId());
        assertEquals(entity.getPlate(), actualVehicle.getPlate().getValue());
        assertEquals(entity.getBrand(), actualVehicle.getBrand());
        assertEquals(entity.getModel(), actualVehicle.getModel());
        assertEquals(entity.getYear(), actualVehicle.getYear());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando vehicle não informado")
    void shouldMapToNullWhenVehicleIsNull() {
        assertNull(VehicleMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando vehicleJpaEntity não informado")
    void shouldMapToNullWhenVehicleJpaEntityIsNull() {
        assertNull(VehicleMapper.toDomain(null));
    }
}
