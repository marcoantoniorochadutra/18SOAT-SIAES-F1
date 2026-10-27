package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase.DecideQuotationByTokenCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalDocumentMismatchException;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationApprovalTokenRepository;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Decide Quotation By Token")
class DecideQuotationByTokenAppServiceTest {

    private static final String RAW_TOKEN = "raw-token";

    @Mock
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Mock
    private QuotationApprovalTokenRepository quotationApprovalTokenRepository;

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DecideQuotationUseCase decideQuotationUseCase;

    @InjectMocks
    private DecideQuotationByTokenAppService decideQuotationByTokenAppService;

    @Test
    @DisplayName("Deve usar o token e aprovar o orçamento quando os dígitos do documento conferem")
    void shouldApproveWithMatchingDocument() {
        Customer customer = createCustomer();
        QuotationApprovalToken token = this.mockTokenOf(customer);

        this.decideQuotationByTokenAppService.execute(this.command(QuotationDecision.APPROVE, this.lastDigitsOf(customer)));

        assertNotNull(token.getUsedAt());
        verify(this.quotationApprovalTokenRepository).save(token);
        verify(this.decideQuotationUseCase).execute(DecideQuotationCommand.builder()
                                                            .quotationId(token.getQuotationId())
                                                            .decision(QuotationDecision.APPROVE)
                                                            .channel(QuotationDecisionChannel.APPROVAL_LINK)
                                                            .decidedByName(customer.getName())
                                                            .build());
    }

    @Test
    @DisplayName("Deve repassar o motivo ao rejeitar pelo link")
    void shouldRejectWithReason() {
        Customer customer = createCustomer();
        QuotationApprovalToken token = this.mockTokenOf(customer);

        this.decideQuotationByTokenAppService.execute(
                DecideQuotationByTokenCommand.of(RAW_TOKEN, QuotationDecision.REJECT, this.lastDigitsOf(customer), "Muito caro"));

        verify(this.decideQuotationUseCase).execute(DecideQuotationCommand.builder()
                                                            .quotationId(token.getQuotationId())
                                                            .decision(QuotationDecision.REJECT)
                                                            .reason("Muito caro")
                                                            .channel(QuotationDecisionChannel.APPROVAL_LINK)
                                                            .decidedByName(customer.getName())
                                                            .build());
    }

    @Test
    @DisplayName("Deve registrar a tentativa e não decidir quando os dígitos não conferem")
    void shouldRegisterFailedAttemptWhenDocumentDoesNotMatch() {
        Customer customer = createCustomer();
        QuotationApprovalToken token = this.mockTokenOf(customer);
        String wrongDigits = this.lastDigitsOf(customer).equals("0000") ? "1111" : "0000";

        assertThrows(QuotationApprovalDocumentMismatchException.class,
                     () -> this.decideQuotationByTokenAppService.execute(this.command(QuotationDecision.APPROVE, wrongDigits)));

        assertEquals(1, token.getFailedAttempts());
        assertNull(token.getUsedAt());
        verify(this.quotationApprovalTokenRepository).save(token);
        verifyNoInteractions(this.decideQuotationUseCase);
    }

    @Test
    @DisplayName("Não deve decidir com token inválido")
    void shouldNotDecideWithInvalidToken() {
        when(this.quotationApprovalTokenHelper.findActive(RAW_TOKEN)).thenThrow(new QuotationApprovalTokenInvalidException());

        assertThrows(QuotationApprovalTokenInvalidException.class,
                     () -> this.decideQuotationByTokenAppService.execute(this.command(QuotationDecision.APPROVE, "1234")));

        verifyNoInteractions(this.quotationRepository, this.decideQuotationUseCase);
    }

    private QuotationApprovalToken mockTokenOf(Customer customer) {
        Quotation quotation = createQuotation(customer.getId(), QuotationStatus.AWAITING_APPROVAL);
        QuotationApprovalToken token = createApprovalToken(quotation.getId());

        when(this.quotationApprovalTokenHelper.findActive(RAW_TOKEN)).thenReturn(token);
        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        return token;
    }

    private String lastDigitsOf(Customer customer) {
        String document = customer.getDocument().getValue();
        return document.substring(document.length() - 4);
    }

    private DecideQuotationByTokenCommand command(QuotationDecision decision, String lastDigits) {
        return DecideQuotationByTokenCommand.of(RAW_TOKEN, decision, lastDigits, null);
    }
}
