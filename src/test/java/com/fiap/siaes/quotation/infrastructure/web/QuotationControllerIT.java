package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.inventory.InventoryTestFactory;
import com.fiap.siaes.maintenance.MaintenanceTestFactory;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.quotation.QuotationTestFactory;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationMaintenanceItemCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase.UpdateQuotationCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import com.fiap.siaes.vehicle.VehicleTestFactory;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.workorder.WorkOrderTestFactory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Quotation")
class QuotationControllerIT extends IntegrationTestUtils {

    private static final int INITIAL_STOCK = 10;
    private static final int RESERVED_QUANTITY = 2;

    private String managerToken;
    private String mechanicToken;
    private User mechanic;
    private Customer customer;
    private Vehicle vehicle;
    private Supplies supplies;
    private Maintenance maintenance;

    @BeforeEach
    void setUp() {
        this.mechanic = super.persistUser(UserRole.MECHANIC, null);
        this.managerToken = super.loginAs(UserRole.MANAGER);
        this.mechanicToken = super.authenticate(this.mechanic);

        this.customer = persist(super.customerRepository, CustomerTestFactory.createCustomer());
        this.vehicle = persist(super.vehicleRepository, VehicleTestFactory.createVehicle(this.customer.getId()));
        this.supplies = persist(super.suppliesRepository, SuppliesTestFactory.createSupplies());
        this.maintenance = persist(super.maintenanceRepository, MaintenanceTestFactory.createMaintenance());
    }

    @Test
    @DisplayName("[201] Deve criar orçamento e reservar o estoque")
    void shouldCreateQuotationAndReserveStock() throws Exception {
        var workOrder = persist(super.workOrderRepository, WorkOrderTestFactory.createWorkOrder(this.customer.getId(), this.vehicle.getId(),
                                                                                                WorkOrderStatus.IN_DIAGNOSIS, this.mechanic.getId()));
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(this.supplies.getId(), INITIAL_STOCK));

        var command = CreateQuotationCommand.builder()
                .workOrderId(workOrder.getId())
                .description("Revisão")
                .odometer(10000L)
                .supplies(List.of(QuotationSuppliesItemCommand.of(this.supplies.getId(), RESERVED_QUANTITY)))
                .maintenances(List.of(QuotationMaintenanceItemCommand.of(this.maintenance.getId(), 1)))
                .build();

        var id = super.createdId(super.executePost(QuotationController.ENDPOINT, this.mechanicToken, command));

        var quotation = super.quotationRepository.findByIdOrThrowNotFound(new QuotationId(id));
        assertEquals(workOrder.getId(), quotation.getWorkOrderId());
        assertEquals(QuotationStatus.DRAFT, quotation.getLastStatus());
        assertEquals(0, this.expectedTotalAmount().compareTo(quotation.getTotalAmount()));

        var stockItem = super.stockItemRepository.findByIdOrThrowNotFound(this.supplies.getId());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        assertEquals(INITIAL_STOCK - RESERVED_QUANTITY, stockItem.getAvailableQuantity());
    }

    @Test
    @DisplayName("[200] Deve buscar orçamento")
    void shouldGetQuotation() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.AWAITING_APPROVAL);

        super.executeGet(QuotationController.ENDPOINT + "/" + quotation.getId(), this.mechanicToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", quotation.getId().toString()))
                .andExpect(jsonValue("$.workOrderId", quotation.getWorkOrderId().toString()))
                .andExpect(jsonValue("$.status", QuotationStatus.AWAITING_APPROVAL.name()))
                .andExpect(jsonValue("$.totalAmount", quotation.getTotalAmount().doubleValue()));
    }

    @Test
    @DisplayName("[200] Deve atualizar orçamento e ajustar a reserva")
    void shouldUpdateQuotationAndAdjustReservation() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.DRAFT);
        var command = UpdateQuotationCommand.builder()
                .description("Revisão completa")
                .odometer(10500L)
                .supplies(List.of(QuotationSuppliesItemCommand.of(this.supplies.getId(), 3)))
                .maintenances(List.of(QuotationMaintenanceItemCommand.of(this.maintenance.getId(), 1)))
                .build();

        super.executePut(QuotationController.ENDPOINT + "/" + quotation.getId(), this.mechanicToken, command)
                .andExpect(status().isOk());

        var updated = super.quotationRepository.findByIdOrThrowNotFound(quotation.getId());
        assertEquals("Revisão completa", updated.getDescription());
        assertEquals(10500L, updated.getOdometer());
        assertEquals(3, super.stockItemRepository.findByIdOrThrowNotFound(this.supplies.getId()).getReservedQuantity());
    }

    @Test
    @DisplayName("[200] Deve listar orçamentos")
    void shouldListQuotations() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.DRAFT);

        super.executeGet(QuotationController.ENDPOINT, this.mechanicToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", quotation.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve concluir orçamento, aguardar aprovação e enviar o link por e-mail")
    void shouldCompleteQuotationAndSendApprovalEmail() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.DRAFT);

        super.executePost(QuotationController.ENDPOINT + "/" + quotation.getId() + "/completion", this.mechanicToken, null)
                .andExpect(status().isNoContent());

        assertEquals(QuotationStatus.AWAITING_APPROVAL, super.quotationRepository.findByIdOrThrowNotFound(quotation.getId()).getLastStatus());
        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, super.workOrderRepository.findByIdOrThrowNotFound(quotation.getWorkOrderId()).getLastStatus());
        verify(super.javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("[200] Deve emitir link de aprovação")
    void shouldIssueApprovalLink() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.AWAITING_APPROVAL);

        super.executePost(QuotationController.ENDPOINT + "/" + quotation.getId() + "/approvalLink", this.mechanicToken, null)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.link", startsWith("http")));
    }

    @Test
    @DisplayName("[204] Cliente deve aprovar orçamento, colocando a OS em execução e consumindo o estoque")
    void shouldApproveQuotationAsCustomer() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.AWAITING_APPROVAL);
        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());

        super.executePatch(QuotationController.ENDPOINT + "/" + quotation.getId() + "/decision", customerToken,
                           DecideQuotationByCustomerCommand.of(null, QuotationDecision.APPROVE, null, null))
                .andExpect(status().isNoContent());

        assertEquals(QuotationStatus.APPROVED, super.quotationRepository.findByIdOrThrowNotFound(quotation.getId()).getLastStatus());
        assertEquals(WorkOrderStatus.IN_EXECUTION, super.workOrderRepository.findByIdOrThrowNotFound(quotation.getWorkOrderId()).getLastStatus());

        var stockItem = super.stockItemRepository.findByIdOrThrowNotFound(this.supplies.getId());
        assertEquals(INITIAL_STOCK - RESERVED_QUANTITY, stockItem.getQuantity());
        assertEquals(0, stockItem.getReservedQuantity());
    }

    @Test
    @DisplayName("[204] Cliente deve rejeitar orçamento e liberar o estoque")
    void shouldRejectQuotationAsCustomer() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.AWAITING_APPROVAL);
        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());

        super.executePatch(QuotationController.ENDPOINT + "/" + quotation.getId() + "/decision", customerToken,
                           DecideQuotationByCustomerCommand.of(null, QuotationDecision.REJECT, null, "Muito caro"))
                .andExpect(status().isNoContent());

        assertEquals(QuotationStatus.REJECTED, super.quotationRepository.findByIdOrThrowNotFound(quotation.getId()).getLastStatus());

        var stockItem = super.stockItemRepository.findByIdOrThrowNotFound(this.supplies.getId());
        assertEquals(INITIAL_STOCK, stockItem.getQuantity());
        assertEquals(0, stockItem.getReservedQuantity());
    }

    @Test
    @DisplayName("[403] Gerente não deve decidir orçamento")
    void shouldNotDecideQuotationAsManager() throws Exception {
        var quotation = this.persistQuotation(QuotationStatus.AWAITING_APPROVAL);

        super.executePatch(QuotationController.ENDPOINT + "/" + quotation.getId() + "/decision", this.managerToken,
                           DecideQuotationByCustomerCommand.of(null, QuotationDecision.APPROVE, null, null))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[403] Cliente não deve criar orçamento")
    void shouldNotCreateQuotationAsCustomer() throws Exception {
        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());

        super.executePost(QuotationController.ENDPOINT, customerToken, null)
                .andExpect(status().isForbidden());
    }

    private Quotation persistQuotation(QuotationStatus status) {
        var workOrderStatus = QuotationStatus.DRAFT.equals(status) ? WorkOrderStatus.IN_DIAGNOSIS : WorkOrderStatus.AWAITING_APPROVAL;
        var workOrder = persist(super.workOrderRepository, WorkOrderTestFactory.createWorkOrder(this.customer.getId(), this.vehicle.getId(),
                                                                                                workOrderStatus, this.mechanic.getId()));
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(this.supplies.getId(), INITIAL_STOCK, RESERVED_QUANTITY));

        return persist(super.quotationRepository, QuotationTestFactory.createQuotation(workOrder.getId(), this.customer.getId(), this.vehicle.getId(),
                                                                                       this.supplies.getId(), this.maintenance.getId(), status));
    }

    private BigDecimal expectedTotalAmount() {
        return this.supplies.getUnitPrice().multiply(BigDecimal.valueOf(RESERVED_QUANTITY)).add(this.maintenance.getPrice());
    }
}
