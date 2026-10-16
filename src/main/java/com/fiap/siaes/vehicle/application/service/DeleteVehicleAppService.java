package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteVehicleAppService implements DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(DeleteVehicleCommand command) {
        Vehicle vehicle = this.findVehicleToDelete(command);
        this.vehicleRepository.deleteById(vehicle.getId());
    }

    private Vehicle findVehicleToDelete(DeleteVehicleCommand command) {
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(command.id());
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer() && authenticatedUser.isDifferentCustomer(vehicle.getCustomerId())) {
            throw new VehicleNotFoundException(command.id().toString());
        }

        return vehicle;
    }
}
