package com.fiap.siaes.workorder.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkOrderResponse(WorkOrderId id, CustomerReferenceResponse customer, VehicleReferenceResponse vehicle,
                                WorkOrderStatus status, List<StatusHistoryResponse> statusHistory, Instant createdAt,
                                Instant finishedAt) {

    public static WorkOrderResponse from(WorkOrder workOrder, Customer customer, Vehicle vehicle) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .customer(CustomerReferenceResponse.from(customer))
                .vehicle(VehicleReferenceResponse.from(vehicle))
                .status(workOrder.getLastStatus())
                .statusHistory(workOrder.getStatusHistory().stream()
                        .sorted(Comparator.comparing(WorkOrderStatusHistory::getUpdatedAt))
                        .map(StatusHistoryResponse::from)
                        .toList())
                .createdAt(workOrder.getCreatedAt())
                .finishedAt(workOrder.getFinishedAt())
                .build();
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
