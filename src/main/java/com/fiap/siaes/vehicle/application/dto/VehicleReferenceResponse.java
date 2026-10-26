package com.fiap.siaes.vehicle.application.dto;

import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public record VehicleReferenceResponse(VehicleId id, String plate, String brand, String model) {

    public static VehicleReferenceResponse of(VehicleId id, String plate, String brand, String model) {
        return new VehicleReferenceResponse(id, plate, brand, model);
    }

    public static VehicleReferenceResponse from(Vehicle vehicle) {
        return VehicleReferenceResponse.of(vehicle.getId(), vehicle.getPlate().getValue(), vehicle.getBrand(), vehicle.getModel());
    }
}
