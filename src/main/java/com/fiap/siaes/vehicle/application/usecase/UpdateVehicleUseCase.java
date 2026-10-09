package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public interface UpdateVehicleUseCase {

    Vehicle execute(VehicleId id, UpdateVehicleCommand command);

    record UpdateVehicleCommand(String brand, String model, int year) {}
}
