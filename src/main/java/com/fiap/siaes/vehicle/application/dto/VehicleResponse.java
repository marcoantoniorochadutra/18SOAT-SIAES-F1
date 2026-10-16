package com.fiap.siaes.vehicle.application.dto;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.persistence.projection.ListVehicleProjection;

import java.util.UUID;

public record VehicleResponse(
        VehicleId id,
        String plate,
        String model,
        int year,
        VehicleOwnerResponse owner) {

    public record VehicleOwnerResponse(CustomerId id, String name) {
        public static VehicleOwnerResponse from(UUID id, String name) {
            return new VehicleOwnerResponse(CustomerId.from(id), name);
        }
    }

    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                vehicle.getModel(),
                vehicle.getYear(),
                new VehicleOwnerResponse(vehicle.getCustomerId(), null));
    }

    public static VehicleResponse from(Vehicle vehicle, Customer customer) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                vehicle.getModel(),
                vehicle.getYear(),
                new VehicleOwnerResponse(customer.getId(), customer.getName()));
    }

    public static VehicleResponse from(ListVehicleProjection vehicle) {
        return new VehicleResponse(
                new VehicleId(vehicle.getId()),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getYear(),
                VehicleOwnerResponse.from(vehicle.getCustomerId(), vehicle.getCustomerName()));
    }
}
