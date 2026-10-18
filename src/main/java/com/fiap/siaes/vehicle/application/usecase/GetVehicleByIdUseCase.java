package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public interface GetVehicleByIdUseCase {

    VehicleResponse execute(GetVehicleByIdCommand command);

    record GetVehicleByIdCommand(VehicleId id, AuthenticatedUser authenticatedUser) {
        public static GetVehicleByIdCommand from(VehicleId id, AuthenticatedUser authenticatedUser) {
            return new GetVehicleByIdCommand(id, authenticatedUser);
        }
    }
}
