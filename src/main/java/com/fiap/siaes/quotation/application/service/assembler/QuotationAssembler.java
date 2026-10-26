package com.fiap.siaes.quotation.application.service.assembler;

import com.fiap.siaes.inventory.domain.exception.InactiveSuppliesException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationMaintenanceItemCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.domain.exception.InactiveMaintenanceException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fiap.siaes.sk.util.FunctionalUtils.nullSafeStream;

@Component
@RequiredArgsConstructor
public class QuotationAssembler {

    private final SuppliesRepository suppliesRepository;
    private final MaintenanceRepository maintenanceRepository;

    public Quotation toQuotation(CreateQuotationCommand command, WorkOrder workOrder) {
        return Quotation.builder()
                .workOrderId(workOrder.getId())
                .customerId(workOrder.getCustomerId())
                .vehicleId(workOrder.getVehicleId())
                .description(command.description())
                .odometer(command.odometer())
                .supplies(this.resolveSupplies(command.supplies()))
                .maintenances(this.resolveMaintenances(command.maintenances()))
                .build();
    }

    public List<QuotationSupplies> resolveSupplies(List<QuotationSuppliesItemCommand> items) {
        return nullSafeStream(items)
                .map(this::toQuotationSupplies)
                .toList();
    }

    public List<QuotationMaintenance> resolveMaintenances(List<QuotationMaintenanceItemCommand> items) {
        return nullSafeStream(items)
                .map(this::toQuotationMaintenance)
                .toList();
    }

    private QuotationSupplies toQuotationSupplies(QuotationSuppliesItemCommand item) {
        Supplies supplies = this.suppliesRepository.findByIdOrThrowNotFound(item.suppliesId());

        if (supplies.isDeactivated())
            throw new InactiveSuppliesException(supplies.getId().toString());

        return QuotationSupplies.builder()
                .suppliesId(supplies.getId())
                .description(supplies.getDescription())
                .unitPrice(supplies.getUnitPrice())
                .quantity(item.quantity())
                .build();
    }

    private QuotationMaintenance toQuotationMaintenance(QuotationMaintenanceItemCommand item) {
        Maintenance maintenance = this.maintenanceRepository.findByIdOrThrowNotFound(item.maintenanceId());

        if (maintenance.isDeactivated())
            throw new InactiveMaintenanceException(maintenance.getId().toString());

        return QuotationMaintenance.builder()
                .maintenanceId(maintenance.getId())
                .description(maintenance.getDescription())
                .unitPrice(maintenance.getPrice())
                .quantity(item.quantity())
                .build();
    }
}
