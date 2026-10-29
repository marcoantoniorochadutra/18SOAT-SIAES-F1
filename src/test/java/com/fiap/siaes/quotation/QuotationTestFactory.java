package com.fiap.siaes.quotation;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationStatusHistory;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class QuotationTestFactory {

    public static final String DESCRIPTION = "Revisão dos 50 mil km";
    public static final long ODOMETER = 50_000L;
    public static final String SUPPLIES_DESCRIPTION = "Filtro de óleo";
    public static final BigDecimal SUPPLIES_UNIT_PRICE = new BigDecimal("45.90");
    public static final String MAINTENANCE_DESCRIPTION = "Troca de óleo";
    public static final BigDecimal MAINTENANCE_UNIT_PRICE = new BigDecimal("80.00");

    public static QuotationSupplies createQuotationSupplies(SuppliesId suppliesId, int quantity) {
        return new QuotationSupplies(suppliesId, SUPPLIES_DESCRIPTION, SUPPLIES_UNIT_PRICE, quantity);
    }

    public static QuotationMaintenance createQuotationMaintenance(MaintenanceId maintenanceId, int quantity) {
        return new QuotationMaintenance(maintenanceId, MAINTENANCE_DESCRIPTION, MAINTENANCE_UNIT_PRICE, quantity);
    }

    public static Quotation createNewQuotation() {
        return Quotation.builder()
                .workOrderId(WorkOrderId.generate())
                .customerId(CustomerId.generate())
                .vehicleId(VehicleId.generate())
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .supplies(List.of(createQuotationSupplies(SuppliesId.generate(), 2)))
                .maintenances(List.of(createQuotationMaintenance(MaintenanceId.generate(), 1)))
                .build();
    }

    public static Quotation createQuotation(WorkOrderId workOrderId, CustomerId customerId, VehicleId vehicleId,
                                            SuppliesId suppliesId, MaintenanceId maintenanceId, QuotationStatus status) {
        Set<QuotationStatusHistory> statusHistory = new HashSet<>();
        statusHistory.add(new QuotationStatusHistory(QuotationStatusHistoryId.generate(), status, Instant.now()));

        return Quotation.recreate()
                .id(QuotationId.generate())
                .workOrderId(workOrderId)
                .customerId(customerId)
                .vehicleId(vehicleId)
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .lastStatus(status)
                .statusHistory(statusHistory)
                .supplies(new ArrayList<>(List.of(createQuotationSupplies(suppliesId, 2))))
                .maintenances(new ArrayList<>(List.of(createQuotationMaintenance(maintenanceId, 1))))
                .createdAt(Instant.now())
                .build();
    }

    public static Quotation createQuotation() {
        return createQuotation(CustomerId.generate(), QuotationStatus.AWAITING_APPROVAL);
    }

    public static Quotation createQuotation(CustomerId customerId, QuotationStatus status) {
        return createQuotation(customerId, status, List.of(createQuotationSupplies(SuppliesId.generate(), 2)));
    }

    public static Quotation createQuotation(CustomerId customerId, QuotationStatus status, List<QuotationSupplies> supplies) {
        Set<QuotationStatusHistory> statusHistory = new HashSet<>();
        statusHistory.add(new QuotationStatusHistory(QuotationStatusHistoryId.generate(), status, Instant.now()));

        return Quotation.recreate()
                .id(QuotationId.generate())
                .workOrderId(WorkOrderId.generate())
                .customerId(customerId)
                .vehicleId(VehicleId.generate())
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .lastStatus(status)
                .statusHistory(statusHistory)
                .supplies(new ArrayList<>(supplies))
                .maintenances(new ArrayList<>(List.of(createQuotationMaintenance(MaintenanceId.generate(), 1))))
                .createdAt(Instant.now())
                .build();
    }

    public static QuotationApprovalToken createApprovalToken(QuotationId quotationId) {
        return createApprovalToken(quotationId, Instant.now().plusSeconds(3600));
    }

    public static QuotationApprovalToken createApprovalToken(QuotationId quotationId, Instant expiresAt) {
        return QuotationApprovalToken.recreate()
                .id(QuotationApprovalTokenId.generate())
                .quotationId(quotationId)
                .expiresAt(expiresAt)
                .createdAt(Instant.now().minusSeconds(60))
                .build();
    }
}
