package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import jakarta.validation.constraints.NotNull;

public interface CreateWorkOrderUseCase {

    WorkOrderId execute(CreateWorkOrderCommand command);

    record CreateWorkOrderCommand(
            @NotNull(message = "{WorkOrderCommand.customerId.notNull}")
            CustomerId customerId,

            @NotNull(message = "{WorkOrderCommand.vehicleId.notNull}")
            VehicleId vehicleId) {

        public static CreateWorkOrderCommand of(CustomerId customerId, VehicleId vehicleId) {
            return new CreateWorkOrderCommand(customerId, vehicleId);
        }
    }
}
