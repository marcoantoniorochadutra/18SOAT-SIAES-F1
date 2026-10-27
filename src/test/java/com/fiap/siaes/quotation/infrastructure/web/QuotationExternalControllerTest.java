package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase.DecideQuotationByTokenCommand;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase.GetQuotationByApprovalTokenCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalDocumentMismatchException;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.utils.ControllerTestUtils;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuotationExternalController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Quotation Approval")
class QuotationExternalControllerTest extends ControllerTestUtils {

    private static final String TOKEN = "0b6f1b5e-7c1e-4b8e-9d55-3c1a2f4e5d6c.signature";

    @MockitoBean
    private GetQuotationByApprovalTokenUseCase getQuotationByApprovalTokenUseCase;

    @MockitoBean
    private DecideQuotationByTokenUseCase decideQuotationByTokenUseCase;

    @Test
    @DisplayName("[200] Deve consultar o orçamento pelo link sem autenticação")
    void shouldGetQuotationByToken() throws Exception {
        var quotation = createQuotation();
        var customer = createCustomer();
        var response = QuotationApprovalResponse.of(QuotationResponse.from(quotation, CustomerReferenceResponse.from(customer), VehicleReferenceResponse.from(createVehicle())), Instant.now());

        when(this.getQuotationByApprovalTokenUseCase.execute(GetQuotationByApprovalTokenCommand.of(TOKEN))).thenReturn(response);

        super.executeGet(QuotationExternalController.ENDPOINT + "/" + TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quotation.id").value(quotation.getId().toString()))
                .andExpect(jsonPath("$.quotation.customer.name").value(customer.getName()))
                .andExpect(jsonPath("$.quotation.vehicle.plate").value(PLATE));
    }

    @Test
    @DisplayName("[404] Não deve consultar o orçamento com link inválido")
    void shouldNotGetQuotationWithInvalidToken() throws Exception {
        when(this.getQuotationByApprovalTokenUseCase.execute(GetQuotationByApprovalTokenCommand.of(TOKEN)))
                .thenThrow(new QuotationApprovalTokenInvalidException());

        super.executeGet(QuotationExternalController.ENDPOINT + "/" + TOKEN)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[204] Deve aprovar o orçamento pelo link")
    void shouldApproveByToken() throws Exception {
        var command = DecideQuotationByTokenCommand.of(null, QuotationDecision.APPROVE, "1234", null);

        super.executePost(QuotationExternalController.ENDPOINT + "/" + TOKEN + "/decision", command)
                .andExpect(status().isNoContent());

        verify(this.decideQuotationByTokenUseCase).execute(command.with(TOKEN));
    }

    @Test
    @DisplayName("[204] Deve rejeitar o orçamento pelo link informando o motivo")
    void shouldRejectByToken() throws Exception {
        var command = DecideQuotationByTokenCommand.of(null, QuotationDecision.REJECT, "1234", "Muito caro");

        super.executePost(QuotationExternalController.ENDPOINT + "/" + TOKEN + "/decision", command)
                .andExpect(status().isNoContent());

        verify(this.decideQuotationByTokenUseCase).execute(command.with(TOKEN));
    }

    @Test
    @DisplayName("[400] Não deve decidir pelo link sem informar a decisão")
    void shouldNotDecideByTokenWithoutDecision() throws Exception {
        var command = DecideQuotationByTokenCommand.of(null, null, "1234", null);

        super.executePost(QuotationExternalController.ENDPOINT + "/" + TOKEN + "/decision", command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("QuotationDecisionCommand.decision.notNull"));

        verifyNoInteractions(this.decideQuotationByTokenUseCase);
    }

    @Test
    @DisplayName("[400] Não deve decidir sem informar os 4 últimos dígitos do documento")
    void shouldNotDecideWithoutDocumentDigits() throws Exception {
        var command = DecideQuotationByTokenCommand.of(null, QuotationDecision.APPROVE, "12", null);

        super.executePost(QuotationExternalController.ENDPOINT + "/" + TOKEN + "/decision", command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("QuotationDecisionCommand.documentLastDigits.pattern"));

        verifyNoInteractions(this.decideQuotationByTokenUseCase);
    }

    @Test
    @DisplayName("[400] Não deve decidir quando os dígitos do documento não conferem")
    void shouldNotDecideWhenDocumentDoesNotMatch() throws Exception {
        var command = DecideQuotationByTokenCommand.of(null, QuotationDecision.APPROVE, "9999", null);

        doThrow(new QuotationApprovalDocumentMismatchException())
                .when(this.decideQuotationByTokenUseCase).execute(command.with(TOKEN));

        super.executePost(QuotationExternalController.ENDPOINT + "/" + TOKEN + "/decision", command)
                .andExpect(status().isBadRequest());
    }
}
