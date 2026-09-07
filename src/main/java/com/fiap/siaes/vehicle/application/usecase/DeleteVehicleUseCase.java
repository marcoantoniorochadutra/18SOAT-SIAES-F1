package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.vehicle.domain.model.VehicleId;

public interface DeleteVehicleUseCase {

    void execute(VehicleId id);
}
