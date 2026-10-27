package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase;
import com.fiap.siaes.workorder.domain.exception.WorkOrderVehicleCustomerMismatchException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateWorkOrderAppService implements CreateWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WorkOrderId execute(CreateWorkOrderCommand command) {
        this.validateCommand(command);

        WorkOrder workOrder = WorkOrder.builder()
                .customerId(command.customerId())
                .vehicleId(command.vehicleId())
                .build();

        this.workOrderRepository.save(workOrder);

        return workOrder.getId();
    }

    private void validateCommand(CreateWorkOrderCommand command) {
        this.customerRepository.existsByIdOrThrowNotFound(command.customerId());
        this.vehicleRepository.existsByIdOrThrowNotFound(command.vehicleId());

        if (!this.vehicleRepository.existsByIdAndCustomerId(command.vehicleId(), command.customerId()))
            throw new WorkOrderVehicleCustomerMismatchException(command.vehicleId().toString(), command.customerId().toString());
    }
}
