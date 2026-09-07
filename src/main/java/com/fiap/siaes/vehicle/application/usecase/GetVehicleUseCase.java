package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.VehicleId;

public interface GetVehicleUseCase {

    Vehicle execute(VehicleId id);
}
