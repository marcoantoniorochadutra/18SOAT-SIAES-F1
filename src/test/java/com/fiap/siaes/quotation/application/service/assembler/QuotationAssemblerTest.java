package com.fiap.siaes.quotation.application.service.assembler;


import com.fiap.siaes.inventory.domain.exception.InactiveSuppliesException;
import com.fiap.siaes.maintenance.MaintenanceTestFactory;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationMaintenanceItemCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.domain.exception.InactiveMaintenanceException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.quotation.QuotationTestFactory.ODOMETER;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Assembler - Quotation")
class QuotationAssemblerTest {

    @Mock
    private SuppliesRepository suppliesRepository;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private QuotationAssembler quotationAssembler;

    @Test
    @DisplayName("Deve montar o orçamento a partir do comando de criação com o cliente e o veículo da ordem de serviço")
    void shouldAssembleQuotationFromCommand() {
        Supplies supplies = SuppliesTestFactory.createSupplies();
        Maintenance maintenance = MaintenanceTestFactory.createMaintenance();
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);
        var command = CreateQuotationCommand.builder()
                .workOrderId(workOrder.getId())
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .supplies(List.of(QuotationSuppliesItemCommand.of(supplies.getId(), 2)))
                .maintenances(List.of(QuotationMaintenanceItemCommand.of(maintenance.getId(), 1)))
                .build();

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);
        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        Quotation quotation = this.quotationAssembler.toQuotation(command, workOrder);

        assertEquals(workOrder.getId(), quotation.getWorkOrderId());
        assertEquals(workOrder.getCustomerId(), quotation.getCustomerId());
        assertEquals(workOrder.getVehicleId(), quotation.getVehicleId());
        assertEquals(DESCRIPTION, quotation.getDescription());
        assertEquals(ODOMETER, quotation.getOdometer());
        assertEquals(QuotationStatus.DRAFT, quotation.getLastStatus());
        assertEquals(1, quotation.getSupplies().size());
        assertEquals(1, quotation.getMaintenances().size());
    }

    @Test
    @DisplayName("Deve copiar a descrição e o preço atual do insumo")
    void shouldSnapshotSuppliesDescriptionAndPrice() {
        Supplies supplies = SuppliesTestFactory.createSupplies();

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        List<QuotationSupplies> result = this.quotationAssembler.resolveSupplies(List.of(
                QuotationSuppliesItemCommand.of(supplies.getId(), 3)));

        QuotationSupplies item = result.getFirst();
        assertEquals(supplies.getId(), item.getSuppliesId());
        assertEquals(supplies.getDescription(), item.getDescription());
        assertEquals(supplies.getUnitPrice(), item.getUnitPrice());
        assertEquals(3, item.getQuantity());
    }

    @Test
    @DisplayName("Deve copiar a descrição e o preço atual do serviço")
    void shouldSnapshotMaintenanceDescriptionAndPrice() {
        Maintenance maintenance = MaintenanceTestFactory.createMaintenance();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        List<QuotationMaintenance> result = this.quotationAssembler.resolveMaintenances(List.of(
                QuotationMaintenanceItemCommand.of(maintenance.getId(), 1)));

        QuotationMaintenance item = result.getFirst();
        assertEquals(maintenance.getId(), item.getMaintenanceId());
        assertEquals(maintenance.getDescription(), item.getDescription());
        assertEquals(maintenance.getPrice(), item.getUnitPrice());
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("Não deve incluir insumo inativo")
    void shouldNotResolveInactiveSupplies() {
        Supplies supplies = SuppliesTestFactory.createSupplies(false);
        var items = List.of(QuotationSuppliesItemCommand.of(supplies.getId(), 1));

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        var exception = assertThrows(InactiveSuppliesException.class, () -> this.quotationAssembler.resolveSupplies(items));

        assertExceptionParameters(exception, supplies.getId().toString());
    }

    @Test
    @DisplayName("Não deve incluir serviço inativo")
    void shouldNotResolveInactiveMaintenance() {
        Maintenance maintenance = MaintenanceTestFactory.createMaintenance(false);
        var items = List.of(QuotationMaintenanceItemCommand.of(maintenance.getId(), 1));

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        var exception = assertThrows(InactiveMaintenanceException.class, () -> this.quotationAssembler.resolveMaintenances(items));

        assertExceptionParameters(exception, maintenance.getId().toString());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando os itens não forem informados")
    void shouldReturnEmptyListWhenItemsAreNull() {
        assertTrue(this.quotationAssembler.resolveSupplies(null).isEmpty());
        assertTrue(this.quotationAssembler.resolveMaintenances(null).isEmpty());

        verifyNoInteractions(this.suppliesRepository, this.maintenanceRepository);
    }
}
