package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public interface CreateVehicleUseCase {

    VehicleId execute(CreateVehicleCommand command);

    record CreateVehicleCommand(String ownerId, String plate, String brand, String model, int year) {}
}
