package com.fiap.siaes.workorder.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ListWorkOrderResponse(WorkOrderId id, CustomerReferenceResponse customer, VehicleReferenceResponse vehicle,
                                    WorkOrderStatus status, Instant createdAt, Instant finishedAt) {
}
