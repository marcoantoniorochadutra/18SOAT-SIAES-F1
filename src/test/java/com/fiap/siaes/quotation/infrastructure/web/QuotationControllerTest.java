package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase.CompleteQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase.GetQuotationByIdCommand;
import com.fiap.siaes.quotation.application.usecase.IssueQuotationApprovalLinkUseCase;
import com.fiap.siaes.quotation.application.usecase.IssueQuotationApprovalLinkUseCase.IssueQuotationApprovalLinkCommand;
import com.fiap.siaes.quotation.application.usecase.ListQuotationsUseCase;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase.UpdateQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationAlreadyOpenException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotAwaitingApprovalException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotEditableException;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.utils.ControllerTestUtils;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.quotation.QuotationTestFactory.ODOMETER;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.quotation.application.dto.QuotationResponse.from;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuotationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Quotation")
class QuotationControllerTest extends ControllerTestUtils {

    @MockitoBean
    private CreateQuotationUseCase createQuotationUseCase;

    @MockitoBean
    private UpdateQuotationUseCase updateQuotationUseCase;

    @MockitoBean
    private CompleteQuotationUseCase completeQuotationUseCase;

    @MockitoBean
    private GetQuotationByIdUseCase getQuotationByIdUseCase;

    @MockitoBean
    private ListQuotationsUseCase listQuotationsUseCase;

    @MockitoBean
    private IssueQuotationApprovalLinkUseCase issueQuotationApprovalLinkUseCase;

    @MockitoBean
    private DecideQuotationByCustomerUseCase decideQuotationByCustomerUseCase;

    @Test
    @DisplayName("[201] Deve criar orçamento com sucesso")
    void shouldCreateQuotation() throws Exception {
        var command = this.createCommandBuilder().build();
        var quotationId = QuotationId.generate();

        when(this.createQuotationUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN))).thenReturn(quotationId);

        super.executePostAuthenticated(QuotationController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(quotationId.toString())));

        verify(this.createQuotationUseCase).execute(command.with(AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[400] Não deve criar orçamento sem ordem de serviço")
    void shouldNotCreateQuotationWithoutWorkOrder() throws Exception {
        var command = this.createCommandBuilder().workOrderId(null).build();

        super.executePostAuthenticated(QuotationController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("QuotationCommand.workOrderId.notNull"));

        verifyNoInteractions(this.createQuotationUseCase);
    }

    @Test
    @DisplayName("[400] Não deve criar orçamento com quantidade de item inválida")
    void shouldNotCreateQuotationWithInvalidItemQuantity() throws Exception {
        var command = this.createCommandBuilder()
                .supplies(List.of(QuotationSuppliesItemCommand.of(SuppliesId.generate(), 0)))
                .build();

        super.executePostAuthenticated(QuotationController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("QuotationCommand.quantity.positive"));

        verifyNoInteractions(this.createQuotationUseCase);
    }

    @Test
    @DisplayName("[409] Não deve criar orçamento quando a ordem de serviço já possuir orçamento em aberto")
    void shouldNotCreateQuotationWhenAlreadyOpen() throws Exception {
        var command = this.createCommandBuilder().build();

        when(this.createQuotationUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new QuotationAlreadyOpenException(command.workOrderId().toString()));

        super.executePostAuthenticated(QuotationController.ENDPOINT, command)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[200] Deve atualizar orçamento com sucesso")
    void shouldUpdateQuotation() throws Exception {
        var id = QuotationId.generate();
        var command = this.updateCommand();

        when(this.updateQuotationUseCase.execute(command.with(id, AUTHENTICATED_USER_ADMIN))).thenReturn(id);

        super.executePutAuthenticated(QuotationController.ENDPOINT + "/" + id, command)
                .andExpect(status().isOk())
                .andExpect(header().string("Location", containsString(id.toString())));

        verify(this.updateQuotationUseCase).execute(command.with(id, AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[409] Não deve atualizar orçamento que não aguarda aprovação")
    void shouldNotUpdateQuotationNotEditable() throws Exception {
        var id = QuotationId.generate();
        var command = this.updateCommand();

        when(this.updateQuotationUseCase.execute(command.with(id, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new QuotationNotEditableException(id.toString(), "APPROVED"));

        super.executePutAuthenticated(QuotationController.ENDPOINT + "/" + id, command)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[200] Deve buscar orçamento pelo id")
    void shouldGetQuotationById() throws Exception {
        var quotation = createQuotation();

        when(this.getQuotationByIdUseCase.execute(GetQuotationByIdCommand.of(quotation.getId(), AUTHENTICATED_USER_ADMIN)))
                .thenReturn(from(quotation, createCustomer(), createVehicle()));

        super.executeGetAuthenticated(QuotationController.ENDPOINT + "/" + quotation.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quotation.getId().toString()))
                .andExpect(jsonPath("$.status").value("AWAITING_APPROVAL"))
                .andExpect(jsonPath("$.vehicle.plate").value(PLATE))
                .andExpect(jsonPath("$.supplies.length()").value(1));
    }

    @Test
    @DisplayName("[200] Deve retornar o link de aprovação para a oficina")
    void shouldGetApprovalLink() throws Exception {
        var id = QuotationId.generate();
        var response = QuotationApprovalLinkResponse.of("https://siaes.com/quotationApprovals/token", Instant.now());

        when(this.issueQuotationApprovalLinkUseCase.execute(IssueQuotationApprovalLinkCommand.of(id))).thenReturn(response);

        super.executePostAuthenticated(QuotationController.ENDPOINT + "/" + id + "/approvalLink", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value(response.link()));
    }

    @Test
    @DisplayName("[409] Não deve retornar link de orçamento que não aguarda aprovação")
    void shouldNotGetApprovalLinkWhenQuotationAlreadyDecided() throws Exception {
        var id = QuotationId.generate();

        when(this.issueQuotationApprovalLinkUseCase.execute(IssueQuotationApprovalLinkCommand.of(id)))
                .thenThrow(new QuotationNotAwaitingApprovalException(id.toString(), "APPROVED"));

        super.executePostAuthenticated(QuotationController.ENDPOINT + "/" + id + "/approvalLink", null)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[204] Deve permitir que o cliente aprove o próprio orçamento")
    void shouldApproveOwnQuotation() throws Exception {
        var id = QuotationId.generate();
        var customer = UserTestFactory.authenticatedUserCustomer(CustomerId.generate());
        var command = DecideQuotationByCustomerCommand.of(null, QuotationDecision.APPROVE, null, null);

        super.executePatchAuthenticated(QuotationController.ENDPOINT + "/" + id + "/decision", command, customer)
                .andExpect(status().isNoContent());

        verify(this.decideQuotationByCustomerUseCase).execute(command.with(id, customer));
    }

    @Test
    @DisplayName("[204] Deve permitir que o cliente rejeite o próprio orçamento informando o motivo")
    void shouldRejectOwnQuotation() throws Exception {
        var id = QuotationId.generate();
        var customer = UserTestFactory.authenticatedUserCustomer(CustomerId.generate());
        var command = DecideQuotationByCustomerCommand.of(null, QuotationDecision.REJECT, null, "Muito caro");

        super.executePatchAuthenticated(QuotationController.ENDPOINT + "/" + id + "/decision", command, customer)
                .andExpect(status().isNoContent());

        verify(this.decideQuotationByCustomerUseCase).execute(command.with(id, customer));
    }

    @Test
    @DisplayName("[400] Não deve decidir sem informar a decisão")
    void shouldNotDecideWithoutDecision() throws Exception {
        var id = QuotationId.generate();
        var command = DecideQuotationByCustomerCommand.of(null, null, null, null);

        super.executePatchAuthenticated(QuotationController.ENDPOINT + "/" + id + "/decision", command,
                                        UserTestFactory.authenticatedUserCustomer(CustomerId.generate()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("decision"));

        verifyNoInteractions(this.decideQuotationByCustomerUseCase);
    }

    @Test
    @DisplayName("[400] Não deve rejeitar com motivo acima do limite")
    void shouldNotRejectWithTooLongReason() throws Exception {
        var id = QuotationId.generate();
        var command = DecideQuotationByCustomerCommand.of(null, QuotationDecision.REJECT, null, "x".repeat(501));

        super.executePatchAuthenticated(QuotationController.ENDPOINT + "/" + id + "/decision", command,
                                        UserTestFactory.authenticatedUserCustomer(CustomerId.generate()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("reason"));

        verifyNoInteractions(this.decideQuotationByCustomerUseCase);
    }

    @Test
    @DisplayName("[200] Deve listar orçamentos com sucesso")
    void shouldListQuotations() throws Exception {
        var response = ListQuotationResponse.builder()
                .id(QuotationId.generate())
                .workOrderId(WorkOrderId.generate())
                .customer(CustomerReferenceResponse.of(CustomerId.generate(), "Maria Silva"))
                .vehicle(VehicleReferenceResponse.of(VehicleId.generate(), PLATE, "Fiat", "Uno"))
                .description(DESCRIPTION)
                .status(QuotationStatus.AWAITING_APPROVAL)
                .totalAmount(new BigDecimal("125.90"))
                .createdAt(Instant.now())
                .build();

        when(this.listQuotationsUseCase.execute(any())).thenReturn(new PageResponse<>(List.of(response), false, 0, 20));

        super.executeGetAuthenticated(QuotationController.ENDPOINT + "?status=AWAITING_APPROVAL")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id().toString()))
                .andExpect(jsonPath("$.content[0].status").value("AWAITING_APPROVAL"))
                .andExpect(jsonPath("$.content[0].customer.name").value("Maria Silva"))
                .andExpect(jsonPath("$.content[0].vehicle.plate").value(PLATE))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar orçamentos com tamanho de página inválido")
    void shouldNotListQuotationsWhenPageSizeExceedsLimit() throws Exception {
        super.executeGetAuthenticated(QuotationController.ENDPOINT + "?size=101")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"));

        verifyNoInteractions(this.listQuotationsUseCase);
    }

    private CreateQuotationCommand.CreateQuotationCommandBuilder createCommandBuilder() {
        return CreateQuotationCommand.builder()
                .workOrderId(WorkOrderId.generate())
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .supplies(List.of(QuotationSuppliesItemCommand.of(SuppliesId.generate(), 2)))
                .maintenances(List.of());
    }

    private UpdateQuotationCommand updateCommand() {
        return UpdateQuotationCommand.builder()
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .supplies(List.of(QuotationSuppliesItemCommand.of(SuppliesId.generate(), 2)))
                .build();
    }

    @Test
    @DisplayName("[204] Deve concluir o orçamento")
    void shouldCompleteQuotation() throws Exception {
        var id = QuotationId.generate();

        super.executePostAuthenticated(QuotationController.ENDPOINT + "/" + id + "/completion", null)
                .andExpect(status().isNoContent());

        verify(this.completeQuotationUseCase).execute(CompleteQuotationCommand.of(id));
    }

    @Test
    @DisplayName("[409] Não deve concluir orçamento que não está em rascunho")
    void shouldNotCompleteQuotationNotDraft() throws Exception {
        var id = QuotationId.generate();

        doThrow(new QuotationNotEditableException(id.toString(), QuotationStatus.AWAITING_APPROVAL.name()))
                .when(this.completeQuotationUseCase).execute(CompleteQuotationCommand.of(id));

        super.executePostAuthenticated(QuotationController.ENDPOINT + "/" + id + "/completion", null)
                .andExpect(status().isConflict());
    }
}
