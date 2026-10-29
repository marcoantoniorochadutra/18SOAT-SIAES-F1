package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.inventory.InventoryTestFactory;
import com.fiap.siaes.maintenance.MaintenanceTestFactory;
import com.fiap.siaes.quotation.QuotationTestFactory;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase.DecideQuotationByTokenCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.supplies.SuppliesTestFactory;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import com.fiap.siaes.vehicle.VehicleTestFactory;
import com.fiap.siaes.workorder.WorkOrderTestFactory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Quotation External")
class QuotationExternalControllerIT extends IntegrationTestUtils {

    private Customer customer;
    private Quotation quotation;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        var mechanic = super.persistUser(UserRole.MECHANIC, null);

        this.customer = persist(super.customerRepository, CustomerTestFactory.createCustomer());
        var vehicle = persist(super.vehicleRepository, VehicleTestFactory.createVehicle(this.customer.getId()));
        var workOrder = persist(super.workOrderRepository,
                                WorkOrderTestFactory.createWorkOrder(this.customer.getId(), vehicle.getId(), WorkOrderStatus.AWAITING_APPROVAL, mechanic.getId()));
        var supplies = persist(super.suppliesRepository, SuppliesTestFactory.createSupplies());
        persist(super.stockItemRepository, InventoryTestFactory.createStockItem(supplies.getId(), 10, 2));
        var maintenance = persist(super.maintenanceRepository, MaintenanceTestFactory.createMaintenance());
        this.quotation = persist(super.quotationRepository,
                                 QuotationTestFactory.createQuotation(workOrder.getId(), this.customer.getId(), vehicle.getId(),
                                                                      supplies.getId(), maintenance.getId(), QuotationStatus.AWAITING_APPROVAL));

        String link = super.read(super.executePost(QuotationController.ENDPOINT + "/" + this.quotation.getId() + "/approvalLink", super.authenticate(mechanic), null), "$.link");
        this.token = link.substring(link.lastIndexOf('/') + 1);
    }

    @Test
    @DisplayName("[200] Deve buscar orçamento pelo token sem autenticação")
    void shouldGetQuotationByToken() throws Exception {
        super.executeGet(QuotationExternalController.ENDPOINT + this.token, null)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.quotation.id", this.quotation.getId().toString()))
                .andExpect(jsonValue("$.quotation.vehicle.plate", VehicleTestFactory.PLATE));
    }

    @Test
    @DisplayName("[204] Deve aprovar orçamento pelo token")
    void shouldApproveQuotationByToken() throws Exception {
        super.executePost(QuotationExternalController.ENDPOINT + this.token + "/decision", null,
                          DecideQuotationByTokenCommand.of(null, QuotationDecision.APPROVE, this.documentLastDigits(), null))
                .andExpect(status().isNoContent());

        assertEquals(QuotationStatus.APPROVED, super.quotationRepository.findByIdOrThrowNotFound(this.quotation.getId()).getLastStatus());
    }

    @Test
    @DisplayName("[400] Não deve decidir orçamento com dígitos do documento incorretos")
    void shouldNotDecideQuotationWithWrongDocumentDigits() throws Exception {
        var wrongDigits = "0000".equals(this.documentLastDigits()) ? "1111" : "0000";

        super.executePost(QuotationExternalController.ENDPOINT + this.token + "/decision", null,
                          DecideQuotationByTokenCommand.of(null, QuotationDecision.APPROVE, wrongDigits, null))
                .andExpect(status().isBadRequest());

        assertEquals(QuotationStatus.AWAITING_APPROVAL, super.quotationRepository.findByIdOrThrowNotFound(this.quotation.getId()).getLastStatus());
    }

    @Test
    @DisplayName("[4xx] Não deve buscar orçamento com token inválido")
    void shouldNotGetQuotationWithInvalidToken() throws Exception {
        super.executeGet(QuotationExternalController.ENDPOINT + "invalid-token", null)
                .andExpect(status().is4xxClientError());
    }

    private String documentLastDigits() {
        var document = this.customer.getDocument().getValue();

        return document.substring(document.length() - 4);
    }
}
