package com.fiap.siaes.workorder.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderStatusHistoryJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.ListWorkOrderProjection;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@UtilityClass
public class WorkOrderMapper {

    public static WorkOrderJpaEntity toEntity(WorkOrder workOrder) {
        if (isNull(workOrder))
            return null;

        return WorkOrderJpaEntity.builder()
                .id(workOrder.getId())
                .customerId(workOrder.getCustomerId())
                .vehicleId(workOrder.getVehicleId())
                .lastStatus(workOrder.getLastStatus())
                .statusHistory(workOrder.getStatusHistory().stream()
                                       .map(WorkOrderMapper::toEntity)
                                       .collect(Collectors.toCollection(HashSet::new)))
                .createdAt(workOrder.getCreatedAt())
                .finishedAt(workOrder.getFinishedAt())
                .version(workOrder.getVersion())
                .build();
    }

    public static WorkOrder toDomain(WorkOrderJpaEntity entity) {
        if (isNull(entity))
            return null;

        return WorkOrder.recreate()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicleId())
                .lastStatus(entity.getLastStatus())
                .statusHistory(entity.getStatusHistory().stream()
                                       .map(WorkOrderMapper::toDomain)
                                       .collect(Collectors.toCollection(HashSet::new)))
                .createdAt(entity.getCreatedAt())
                .finishedAt(entity.getFinishedAt())
                .version(entity.getVersion())
                .build();
    }

    public static ListWorkOrderResponse toResponse(ListWorkOrderProjection projection) {
        return ListWorkOrderResponse.builder()
                .id(WorkOrderId.from(projection.getId()))
                .customer(CustomerReferenceResponse.of(CustomerId.from(projection.getCustomerId()), projection.getCustomerName()))
                .vehicle(VehicleReferenceResponse.of(VehicleId.from(projection.getVehicleId()), projection.getVehiclePlate(),
                                                     projection.getVehicleBrand(), projection.getVehicleModel()))
                .status(WorkOrderStatus.valueOf(projection.getStatus()))
                .createdAt(projection.getCreatedAt())
                .finishedAt(projection.getFinishedAt())
                .build();
    }

    private static WorkOrderStatusHistoryJpaEntity toEntity(WorkOrderStatusHistory history) {
        return new WorkOrderStatusHistoryJpaEntity(history.getId(), history.getStatus(), history.getUpdatedAt());
    }

    private static WorkOrderStatusHistory toDomain(WorkOrderStatusHistoryJpaEntity entity) {
        return new WorkOrderStatusHistory(entity.getId(), entity.getStatus(), entity.getUpdatedAt());
    }
}
