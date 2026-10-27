package com.fiap.siaes.workorder.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.Diagnosis;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

import static java.util.Objects.isNull;

@Builder
public record WorkOrderResponse(WorkOrderId id, CustomerReferenceResponse customer, VehicleReferenceResponse vehicle,
                                WorkOrderStatus status, List<StatusHistoryResponse> statusHistory,
                                DiagnosisResponse diagnosis, Instant createdAt, Instant finishedAt) {

    public static WorkOrderResponse from(WorkOrder workOrder, CustomerReferenceResponse customer, VehicleReferenceResponse vehicle) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .customer(customer)
                .vehicle(vehicle)
                .status(workOrder.getLastStatus())
                .statusHistory(workOrder.getStatusHistory().stream()
                        .sorted(Comparator.comparing(WorkOrderStatusHistory::getUpdatedAt))
                        .map(StatusHistoryResponse::from)
                        .toList())
                .diagnosis(DiagnosisResponse.from(workOrder.getDiagnosis()))
                .createdAt(workOrder.getCreatedAt())
                .finishedAt(workOrder.getFinishedAt())
                .build();
    }

    public record DiagnosisResponse(String description, UserId diagnosedBy, Instant diagnosedAt) {

        public static DiagnosisResponse of(String description, UserId diagnosedBy, Instant diagnosedAt) {
            return new DiagnosisResponse(description, diagnosedBy, diagnosedAt);
        }

        public static DiagnosisResponse from(Diagnosis diagnosis) {
            if (isNull(diagnosis))
                return null;

            return DiagnosisResponse.of(diagnosis.description(), diagnosis.diagnosedBy(), diagnosis.diagnosedAt());
        }
    }

    public record StatusHistoryResponse(WorkOrderStatus status, Instant updatedAt) {

        public static StatusHistoryResponse of(WorkOrderStatus status, Instant updatedAt) {
            return new StatusHistoryResponse(status, updatedAt);
        }

        public static StatusHistoryResponse from(WorkOrderStatusHistory history) {
            return StatusHistoryResponse.of(history.getStatus(), history.getUpdatedAt());
        }
    }
}
