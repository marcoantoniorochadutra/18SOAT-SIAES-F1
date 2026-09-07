package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.WorkOrderId;

import java.util.List;

public interface CreateWorkOrderUseCase {

    WorkOrderId execute(CreateWorkOrderCommand command);

    record CreateWorkOrderCommand(
            String customerId,
            String vehicleId,
            List<String> requestedServiceIds,
            List<SuppliesLine> supplies
    ) {}

    record SuppliesLine(String suppliesId, int quantity) {}
}
