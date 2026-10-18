package com.fiap.siaes.vehicle.infrastructure.persistence.mapper;

import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse.VehicleOwnerResponse;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fiap.siaes.vehicle.infrastructure.persistence.projection.ListVehicleProjection;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class VehicleMapper {

    public static VehicleJpaEntity toEntity(Vehicle vehicle) {
        if (isNull(vehicle))
            return null;

        return VehicleJpaEntity.builder()
                .id(vehicle.getId())
                .customerId(vehicle.getCustomerId())
                .plate(vehicle.getPlate().getValue())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .build();

    }

    public static Vehicle toDomain(VehicleJpaEntity vehicle) {
        if (isNull(vehicle))
            return null;

        return Vehicle.recreate()
                .id(vehicle.getId())
                .customerId(vehicle.getCustomerId())
                .plate(Plate.recreate(vehicle.getPlate()))
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .build();
    }

    public static VehicleResponse toResponse(ListVehicleProjection projection) {
        return new VehicleResponse(
                new VehicleId(projection.getId()),
                projection.getPlate(),
                projection.getModel(),
                projection.getYear(),
                VehicleOwnerResponse.from(projection.getCustomerId(), projection.getCustomerName()));
    }
}
