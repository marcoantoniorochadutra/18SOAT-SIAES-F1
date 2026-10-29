package com.fiap.siaes.workorder.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.inventory.InventoryTestFactory;
import com.fiap.siaes.maintenance.MaintenanceTestFactory;
import com.fiap.siaes.quotation.QuotationTestFactory;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.infrastructure.web.QuotationController;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import com.fiap.siaes.vehicle.VehicleTestFactory;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.workorder.WorkOrderTestFactory;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase.CreateWorkOrderCommand;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderDiagnosisUseCase.RegisterWorkOrderDiagnosisCommand;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Work Order")
class WorkOrderControllerIT extends IntegrationTestUtils {

    private String managerToken;
    private String mechanicToken;
    private User mechanic;
    private Customer customer;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        this.mechanic = super.persistUser(UserRole.MECHANIC, null);
        this.managerToken = super.loginAs(UserRole.MANAGER);
        this.mechanicToken = super.authenticate(this.mechanic);

        this.customer = persist(super.customerRepository, CustomerTestFactory.createCustomer());
        this.vehicle = persist(super.vehicleRepository, VehicleTestFactory.createVehicle(this.customer.getId()));
    }

    @Test
    @DisplayName("[201] Deve criar ordem de serviço")
    void shouldCreateWorkOrder() throws Exception {
        var command = CreateWorkOrderCommand.of(this.customer.getId(), this.vehicle.getId());

        var id = super.createdId(super.executePost(WorkOrderController.ENDPOINT, this.mechanicToken, command));

        var workOrder = super.workOrderRepository.findByIdOrThrowNotFound(new WorkOrderId(id));
        assertEquals(WorkOrderStatus.RECEIVED, workOrder.getLastStatus());
        assertEquals(this.vehicle.getId(), workOrder.getVehicleId());
    }

    @Test
    @DisplayName("[200] Deve buscar ordem de serviço")
    void shouldGetWorkOrder() throws Exception {
        var workOrder = this.persistWorkOrder();

        super.executeGet(this.url(workOrder), this.mechanicToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", workOrder.getId().toString()))
                .andExpect(jsonValue("$.status", WorkOrderStatus.RECEIVED.name()))
                .andExpect(jsonValue("$.vehicle.plate", this.vehicle.getPlate().getValue()));
    }

    @Test
    @DisplayName("[204] Deve iniciar e registrar o diagnóstico")
    void shouldStartAndRegisterDiagnosis() throws Exception {
        var workOrder = this.persistWorkOrder();
        var command = RegisterWorkOrderDiagnosisCommand.builder().description("Troca de óleo necessária").build();

        super.executePost(this.url(workOrder) + "/diagnosis", this.mechanicToken, null)
                .andExpect(status().isNoContent());
        super.executePut(this.url(workOrder) + "/diagnosis", this.mechanicToken, command)
                .andExpect(status().isNoContent());

        var diagnosed = super.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId());
        assertEquals(WorkOrderStatus.IN_DIAGNOSIS, diagnosed.getLastStatus());
        assertEquals("Troca de óleo necessária", diagnosed.getDiagnosis().description());
    }

    @Test
    @DisplayName("[200] Deve listar ordens de serviço")
    void shouldListWorkOrders() throws Exception {
        var workOrder = this.persistWorkOrder();

        super.executeGet(WorkOrderController.ENDPOINT, this.mechanicToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", workOrder.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve cancelar ordem de serviço")
    void shouldCancelWorkOrder() throws Exception {
        var workOrder = this.persistWorkOrder();

        super.executePost(this.url(workOrder) + "/cancellation", this.mechanicToken, null)
                .andExpect(status().isNoContent());

        assertEquals(WorkOrderStatus.CANCELLED, super.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId()).getLastStatus());
    }

    @Test
    @DisplayName("[204] Deve percorrer o fluxo completo até a entrega e calcular o tempo de execução")
    void shouldRunFullFlowUntilDelivery() throws Exception {
        var workOrder = persist(super.workOrderRepository,
                                WorkOrderTestFactory.createWorkOrder(this.customer.getId(), this.vehicle.getId(), WorkOrderStatus.AWAITING_APPROVAL,
                                                                     this.mechanic.getId()));
        var supplies = persist(super.suppliesRepository, SuppliesTestFactory.createSupplies());
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(supplies.getId(), 10, 2));
        var maintenance = persist(super.maintenanceRepository, MaintenanceTestFactory.createMaintenance());
        var quotation = persist(super.quotationRepository,
                                QuotationTestFactory.createQuotation(workOrder.getId(), this.customer.getId(), this.vehicle.getId(),
                                                                     supplies.getId(), maintenance.getId(), QuotationStatus.AWAITING_APPROVAL));

        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());
        super.executePatch(QuotationController.ENDPOINT + "/" + quotation.getId() + "/decision", customerToken,
                           DecideQuotationByCustomerCommand.of(null, QuotationDecision.APPROVE, null, null))
                .andExpect(status().isNoContent());

        super.executePost(this.url(workOrder) + "/completion", this.mechanicToken, null)
                .andExpect(status().isNoContent());
        super.executePost(this.url(workOrder) + "/delivery", this.mechanicToken, null)
                .andExpect(status().isNoContent());

        var delivered = super.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId());
        assertEquals(WorkOrderStatus.DELIVERED, delivered.getLastStatus());
        assertEquals(6, delivered.getStatusHistory().size());

        super.executeGet(WorkOrderController.ENDPOINT + "/metrics/executionTime", this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.workOrderCount", 1));
    }

    @Test
    @DisplayName("[409] Não deve finalizar ordem de serviço que não está em execução")
    void shouldNotFinishWorkOrderNotInExecution() throws Exception {
        var workOrder = this.persistWorkOrder();

        super.executePost(this.url(workOrder) + "/completion", this.mechanicToken, null)
                .andExpect(status().is4xxClientError());

        assertEquals(WorkOrderStatus.RECEIVED, super.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId()).getLastStatus());
    }

    @Test
    @DisplayName("[403] Mecânico não deve consultar métricas de tempo de execução")
    void shouldNotGetExecutionTimeAsMechanic() throws Exception {
        super.executeGet(WorkOrderController.ENDPOINT + "/metrics/executionTime", this.mechanicToken)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[403] Cliente não deve criar ordem de serviço")
    void shouldNotCreateWorkOrderAsCustomer() throws Exception {
        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());

        super.executePost(WorkOrderController.ENDPOINT, customerToken, null)
                .andExpect(status().isForbidden());
    }

    private WorkOrder persistWorkOrder() {
        return persist(super.workOrderRepository, WorkOrderTestFactory.createWorkOrder(this.customer.getId(), this.vehicle.getId()));
    }

    private String url(WorkOrder workOrder) {
        return WorkOrderController.ENDPOINT + "/" + workOrder.getId();
    }
}
