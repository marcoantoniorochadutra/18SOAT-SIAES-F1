package com.fiap.siaes.workorder.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import com.fiap.siaes.workorder.domain.model.vo.Diagnosis;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderStatusHistoryJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.ListWorkOrderProjection;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.WorkOrderStatusHistoryProjection;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@UtilityClass
public class WorkOrderMapper {

    public static WorkOrderJpaEntity toEntity(WorkOrder workOrder) {
        if (isNull(workOrder))
            return null;

        Diagnosis diagnosis = workOrder.getDiagnosis();

        return WorkOrderJpaEntity.builder()
                .id(workOrder.getId())
                .customerId(workOrder.getCustomerId())
                .vehicleId(workOrder.getVehicleId())
                .lastStatus(workOrder.getLastStatus())
                .statusHistory(workOrder.getStatusHistory().stream()
                                       .map(WorkOrderMapper::toEntity)
                                       .collect(Collectors.toCollection(HashSet::new)))
                .diagnosis(isNull(diagnosis) ? null : diagnosis.description())
                .diagnosedBy(isNull(diagnosis) ? null : diagnosis.diagnosedBy())
                .diagnosedAt(isNull(diagnosis) ? null : diagnosis.diagnosedAt())
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
                .diagnosis(toDiagnosis(entity))
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

    private static Diagnosis toDiagnosis(WorkOrderJpaEntity entity) {
        if (isNull(entity.getDiagnosis()))
            return null;

        return Diagnosis.of(entity.getDiagnosis(), entity.getDiagnosedBy(), entity.getDiagnosedAt());
    }

    public static List<StatusTimeline> toStatusTimelines(List<WorkOrderStatusHistoryProjection> projections) {
        return projections.stream()
                .collect(Collectors.groupingBy(WorkOrderStatusHistoryProjection::getWorkOrderId, LinkedHashMap<UUID, List<WorkOrderStatusHistory>>::new,
                                               Collectors.mapping(WorkOrderMapper::toDomain, Collectors.toList())))
                .values().stream()
                .map(StatusTimeline::of)
                .toList();
    }

    private static WorkOrderStatusHistory toDomain(WorkOrderStatusHistoryProjection projection) {
        return new WorkOrderStatusHistory(new WorkOrderStatusHistoryId(projection.getId()), WorkOrderStatus.valueOf(projection.getStatus()),
                                          projection.getUpdatedAt());
    }

    private static WorkOrderStatusHistoryJpaEntity toEntity(WorkOrderStatusHistory history) {
        return new WorkOrderStatusHistoryJpaEntity(history.getId(), history.getStatus(), history.getUpdatedAt());
    }

    private static WorkOrderStatusHistory toDomain(WorkOrderStatusHistoryJpaEntity entity) {
        return new WorkOrderStatusHistory(entity.getId(), entity.getStatus(), entity.getUpdatedAt());
    }
}
