package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetVehicleByIdAppService implements GetVehicleByIdUseCase {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public VehicleResponse execute(GetVehicleByIdCommand command) {
        Vehicle vehicle = this.findVehicle(command);
        Customer customer = this.customerRepository.findByIdOrThrowNotFound(vehicle.getCustomerId());

        return VehicleResponse.from(vehicle, customer);
    }

    private Vehicle findVehicle(GetVehicleByIdCommand command) {
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(command.id());

        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        if (authenticatedUser.isCustomer() && authenticatedUser.isDifferentCustomer(vehicle.getCustomerId())) {
            throw new VehicleNotFoundException(command.id().toString());
        }

        return vehicle;
    }
}
