package com.fiap.siaes.vehicle.infrastructure.persistence.mapper;

import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpa;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static VehicleJpa toEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new VehicleJpa(
                vehicle.getId(),
                vehicle.getOwnerId(),
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear()
        );
    }

    public static Vehicle toDomain(VehicleJpa entity) {
        if (entity == null) {
            return null;
        }
        return Vehicle.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .plate(entity.getPlate())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .build();
    }
}
