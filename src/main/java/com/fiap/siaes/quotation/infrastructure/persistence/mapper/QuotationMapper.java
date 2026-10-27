package com.fiap.siaes.quotation.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationStatusHistory;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationMaintenanceJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationStatusHistoryJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationSuppliesJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.projection.ListQuotationProjection;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@UtilityClass
public class QuotationMapper {

    public static QuotationJpaEntity toEntity(Quotation quotation) {
        if (isNull(quotation))
            return null;

        return QuotationJpaEntity.builder()
                .id(quotation.getId())
                .workOrderId(quotation.getWorkOrderId())
                .customerId(quotation.getCustomerId())
                .vehicleId(quotation.getVehicleId())
                .description(quotation.getDescription())
                .odometer(quotation.getOdometer())
                .lastStatus(quotation.getLastStatus())
                .statusHistory(quotation.getStatusHistory().stream()
                                       .map(QuotationMapper::toEntity)
                                       .collect(Collectors.toCollection(HashSet::new)))
                .supplies(quotation.getSupplies().stream()
                                  .map(QuotationMapper::toEntity)
                                  .collect(Collectors.toCollection(LinkedHashSet::new)))
                .maintenances(quotation.getMaintenances().stream()
                                      .map(QuotationMapper::toEntity)
                                      .collect(Collectors.toCollection(LinkedHashSet::new)))
                .decisionChannel(quotation.getDecisionChannel())
                .decidedBy(quotation.getDecidedBy())
                .decidedAt(quotation.getDecidedAt())
                .rejectionReason(quotation.getRejectionReason())
                .createdAt(quotation.getCreatedAt())
                .build();
    }

    public static Quotation toDomain(QuotationJpaEntity entity) {
        if (isNull(entity))
            return null;

        return Quotation.recreate()
                .id(entity.getId())
                .workOrderId(entity.getWorkOrderId())
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicleId())
                .description(entity.getDescription())
                .odometer(entity.getOdometer())
                .lastStatus(entity.getLastStatus())
                .statusHistory(entity.getStatusHistory().stream()
                                       .map(QuotationMapper::toDomain)
                                       .collect(Collectors.toCollection(HashSet::new)))
                .supplies(entity.getSupplies().stream()
                                  .map(QuotationMapper::toDomain)
                                  .collect(Collectors.toCollection(ArrayList::new)))
                .maintenances(entity.getMaintenances().stream()
                                      .map(QuotationMapper::toDomain)
                                      .collect(Collectors.toCollection(ArrayList::new)))
                .decisionChannel(entity.getDecisionChannel())
                .decidedBy(entity.getDecidedBy())
                .decidedAt(entity.getDecidedAt())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ListQuotationResponse toResponse(ListQuotationProjection projection) {
        return ListQuotationResponse.builder()
                .id(QuotationId.from(projection.getId()))
                .workOrderId(WorkOrderId.from(projection.getWorkOrderId()))
                .customer(CustomerReferenceResponse.of(CustomerId.from(projection.getCustomerId()), projection.getCustomerName()))
                .vehicle(VehicleReferenceResponse.of(VehicleId.from(projection.getVehicleId()), projection.getVehiclePlate(),
                                                     projection.getVehicleBrand(), projection.getVehicleModel()))
                .description(projection.getDescription())
                .status(QuotationStatus.valueOf(projection.getStatus()))
                .totalAmount(projection.getTotalAmount())
                .createdAt(projection.getCreatedAt())
                .build();
    }

    private static QuotationStatusHistoryJpaEntity toEntity(QuotationStatusHistory history) {
        return new QuotationStatusHistoryJpaEntity(history.getId(), history.getStatus(), history.getUpdatedAt());
    }

    private static QuotationStatusHistory toDomain(QuotationStatusHistoryJpaEntity entity) {
        return new QuotationStatusHistory(entity.getId(), entity.getStatus(), entity.getUpdatedAt());
    }

    private static QuotationSuppliesJpaEntity toEntity(QuotationSupplies supplies) {
        return new QuotationSuppliesJpaEntity(supplies.getId(), supplies.getSuppliesId(), supplies.getDescription(),
                                              supplies.getUnitPrice(), supplies.getQuantity());
    }

    private static QuotationSupplies toDomain(QuotationSuppliesJpaEntity entity) {
        return new QuotationSupplies(entity.getId(), entity.getSuppliesId(), entity.getDescription(),
                                     entity.getUnitPrice(), entity.getQuantity());
    }

    private static QuotationMaintenanceJpaEntity toEntity(QuotationMaintenance maintenance) {
        return new QuotationMaintenanceJpaEntity(maintenance.getId(), maintenance.getMaintenanceId(), maintenance.getDescription(),
                                                 maintenance.getUnitPrice(), maintenance.getQuantity());
    }

    private static QuotationMaintenance toDomain(QuotationMaintenanceJpaEntity entity) {
        return new QuotationMaintenance(entity.getId(), entity.getMaintenanceId(), entity.getDescription(),
                                        entity.getUnitPrice(), entity.getQuantity());
    }
}
