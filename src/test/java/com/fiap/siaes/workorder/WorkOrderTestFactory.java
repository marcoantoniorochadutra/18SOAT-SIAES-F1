package com.fiap.siaes.workorder;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class WorkOrderTestFactory {

    public static WorkOrder createWorkOrder() {
        return createWorkOrder(CustomerId.generate(), WorkOrderStatus.RECEIVED);
    }

    public static WorkOrder createWorkOrder(WorkOrderStatus status) {
        return createWorkOrder(CustomerId.generate(), status);
    }

    public static WorkOrder createWorkOrder(CustomerId customerId, WorkOrderStatus status) {
        Set<WorkOrderStatusHistory> statusHistory = new HashSet<>();
        statusHistory.add(new WorkOrderStatusHistory(WorkOrderStatusHistoryId.generate(), status, Instant.now()));

        return WorkOrder.recreate()
                .id(WorkOrderId.generate())
                .customerId(customerId)
                .vehicleId(VehicleId.generate())
                .lastStatus(status)
                .statusHistory(statusHistory)
                .createdAt(Instant.now())
                .version(0L)
                .build();
    }
}
