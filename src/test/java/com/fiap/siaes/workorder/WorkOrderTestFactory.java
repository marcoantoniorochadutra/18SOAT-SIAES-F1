package com.fiap.siaes.workorder;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class WorkOrderTestFactory {

    public static final Instant RECEIVED_AT = Instant.parse("2026-10-01T08:00:00Z");

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

    public static WorkOrder createFinishedWorkOrder(long receivedMinutes, long diagnosisMinutes, long approvalMinutes, long executionMinutes) {
        StatusTimeline timeline = createFinishedTimeline(receivedMinutes, diagnosisMinutes, approvalMinutes, executionMinutes);

        return WorkOrder.recreate()
                .id(WorkOrderId.generate())
                .customerId(CustomerId.generate())
                .vehicleId(VehicleId.generate())
                .lastStatus(WorkOrderStatus.FINISHED)
                .statusHistory(new HashSet<>(timeline.history()))
                .createdAt(RECEIVED_AT)
                .finishedAt(timeline.history().getLast().getUpdatedAt())
                .version(0L)
                .build();
    }

    public static StatusTimeline createFinishedTimeline(long receivedMinutes, long diagnosisMinutes, long approvalMinutes, long executionMinutes) {
        List<WorkOrderStatus> statuses = List.of(WorkOrderStatus.RECEIVED, WorkOrderStatus.IN_DIAGNOSIS, WorkOrderStatus.AWAITING_APPROVAL,
                                                 WorkOrderStatus.IN_EXECUTION, WorkOrderStatus.FINISHED);
        List<Long> minutesInStatus = List.of(receivedMinutes, diagnosisMinutes, approvalMinutes, executionMinutes, 0L);

        List<WorkOrderStatusHistory> history = new ArrayList<>();
        Instant updatedAt = RECEIVED_AT;
        for (int i = 0; i < statuses.size(); i++) {
            history.add(new WorkOrderStatusHistory(WorkOrderStatusHistoryId.generate(), statuses.get(i), updatedAt));
            updatedAt = updatedAt.plus(Duration.ofMinutes(minutesInStatus.get(i)));
        }

        return StatusTimeline.of(history);
    }
}
