package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public interface DeleteVehicleUseCase {

    void execute(DeleteVehicleCommand command);

    record DeleteVehicleCommand(VehicleId id, AuthenticatedUser authenticatedUser) {
        public static DeleteVehicleCommand of(VehicleId id, AuthenticatedUser authenticatedUser) {
            return new DeleteVehicleCommand(id, authenticatedUser);
        }
    }
}
