package com.fiap.siaes.quotation.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record QuotationResponse(QuotationId id, WorkOrderId workOrderId, CustomerReferenceResponse customer,
                                VehicleReferenceResponse vehicle, String description, long odometer, QuotationStatus status,
                                BigDecimal totalAmount, List<SuppliesItemResponse> supplies,
                                List<MaintenanceItemResponse> maintenances, Instant createdAt) {

    public static QuotationResponse from(Quotation quotation, CustomerReferenceResponse customer, VehicleReferenceResponse vehicle) {
        return QuotationResponse.builder()
                .id(quotation.getId())
                .workOrderId(quotation.getWorkOrderId())
                .customer(customer)
                .vehicle(vehicle)
                .description(quotation.getDescription())
                .odometer(quotation.getOdometer())
                .status(quotation.getLastStatus())
                .totalAmount(quotation.getTotalAmount())
                .supplies(quotation.getSupplies().stream().map(SuppliesItemResponse::from).toList())
                .maintenances(quotation.getMaintenances().stream().map(MaintenanceItemResponse::from).toList())
                .createdAt(quotation.getCreatedAt())
                .build();
    }

    @Builder
    public record SuppliesItemResponse(SuppliesId suppliesId, String description, BigDecimal unitPrice, int quantity,
                                       BigDecimal subtotal) {

        public static SuppliesItemResponse from(QuotationSupplies supplies) {
            return SuppliesItemResponse.builder()
                    .suppliesId(supplies.getSuppliesId())
                    .description(supplies.getDescription())
                    .unitPrice(supplies.getUnitPrice())
                    .quantity(supplies.getQuantity())
                    .subtotal(supplies.getSubtotal())
                    .build();
        }
    }

    @Builder
    public record MaintenanceItemResponse(MaintenanceId maintenanceId, String description, BigDecimal unitPrice, int quantity,
                                          BigDecimal subtotal) {

        public static MaintenanceItemResponse from(QuotationMaintenance maintenance) {
            return MaintenanceItemResponse.builder()
                    .maintenanceId(maintenance.getMaintenanceId())
                    .description(maintenance.getDescription())
                    .unitPrice(maintenance.getUnitPrice())
                    .quantity(maintenance.getQuantity())
                    .subtotal(maintenance.getSubtotal())
                    .build();
        }
    }
}
