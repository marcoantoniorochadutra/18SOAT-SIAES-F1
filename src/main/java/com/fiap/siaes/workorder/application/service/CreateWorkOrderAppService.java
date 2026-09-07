package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase;
import com.fiap.siaes.workorder.domain.model.WorkDomainRepository;
import com.fiap.siaes.workorder.domain.model.WorkOrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateWorkOrderAppService implements CreateWorkOrderUseCase {

    private final WorkDomainRepository workDomainRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional
    public WorkOrderId execute(CreateWorkOrderCommand command) {
       return null;
    }
}
