package com.fiap.siaes.workorder;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.Diagnosis;
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

    public static final String DIAGNOSIS = "Pastilhas de freio dianteiras gastas";

    public static final Instant RECEIVED_AT = Instant.parse("2026-10-01T08:00:00Z");

    public static WorkOrder createWorkOrder() {
        return createWorkOrder(CustomerId.generate(), WorkOrderStatus.RECEIVED);
    }

    public static WorkOrder createWorkOrder(WorkOrderStatus status) {
        return createWorkOrder(CustomerId.generate(), status);
    }

    public static WorkOrder createDiagnosedWorkOrder() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);
        workOrder.registerDiagnosis(DIAGNOSIS, UserId.generate());
        return workOrder;
    }

    public static WorkOrder createWorkOrder(CustomerId customerId, VehicleId vehicleId) {
        return WorkOrder.builder()
                .customerId(customerId)
                .vehicleId(vehicleId)
                .build();
    }

    public static WorkOrder createWorkOrder(CustomerId customerId, VehicleId vehicleId, WorkOrderStatus status, UserId diagnosedBy) {
        List<WorkOrderStatus> flow = List.of(WorkOrderStatus.RECEIVED, WorkOrderStatus.IN_DIAGNOSIS, WorkOrderStatus.AWAITING_APPROVAL,
                                             WorkOrderStatus.IN_EXECUTION, WorkOrderStatus.FINISHED, WorkOrderStatus.DELIVERED);
        List<WorkOrderStatus> statuses = flow.subList(0, flow.indexOf(status) + 1);
        Instant createdAt = Instant.now().minus(Duration.ofMinutes(statuses.size()));

        Set<WorkOrderStatusHistory> statusHistory = new HashSet<>();
        for (int i = 0; i < statuses.size(); i++) {
            statusHistory.add(new WorkOrderStatusHistory(WorkOrderStatusHistoryId.generate(), statuses.get(i), createdAt.plus(Duration.ofMinutes(i))));
        }

        return WorkOrder.recreate()
                .id(WorkOrderId.generate())
                .customerId(customerId)
                .vehicleId(vehicleId)
                .lastStatus(status)
                .statusHistory(statusHistory)
                .diagnosis(Diagnosis.of(DIAGNOSIS, diagnosedBy, createdAt))
                .createdAt(createdAt)
                .build();
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
