package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.application.usecase.DeleteCustomerVehiclesUseCase;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerVehiclesAppService implements DeleteCustomerVehiclesUseCase {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CustomerId customerId) {
        this.vehicleRepository.deleteAllByCustomerId(customerId);
    }
}
