package com.fiap.siaes.vehicle.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

import lombok.Builder;

@Builder
public record VehicleResponse(
        VehicleId id,
        String plate,
        String brand,
        String model,
        int year,
        CustomerReferenceResponse owner) {

    public static VehicleResponse from(Vehicle vehicle, Customer customer) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plate(vehicle.getPlate().getValue())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .owner(CustomerReferenceResponse.from(customer))
                .build();
    }
}
