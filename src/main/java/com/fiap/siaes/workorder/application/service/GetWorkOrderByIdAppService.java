package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.application.dto.WorkOrderResponse;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderByIdUseCase;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetWorkOrderByIdAppService implements GetWorkOrderByIdUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public WorkOrderResponse execute(GetWorkOrderByIdCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.id());

        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        if (authenticatedUser.isCustomer() && authenticatedUser.isDifferentCustomer(workOrder.getCustomerId())) {
            throw new WorkOrderNotFoundException(command.id().toString());
        }

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(workOrder.getCustomerId());
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(workOrder.getVehicleId());
        return WorkOrderResponse.from(workOrder, customer, vehicle);
    }
}
