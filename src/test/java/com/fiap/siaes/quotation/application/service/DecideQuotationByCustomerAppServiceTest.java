package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Decide Quotation By Customer")
class DecideQuotationByCustomerAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private DecideQuotationUseCase decideQuotationUseCase;

    @InjectMocks
    private DecideQuotationByCustomerAppService decideQuotationByCustomerAppService;

    @Test
    @DisplayName("Deve decidir o próprio orçamento registrando o usuário do cliente")
    void shouldDecideOwnQuotation() {
        var customerId = CustomerId.generate();
        var authenticatedUser = UserTestFactory.authenticatedUserCustomer(customerId);
        Quotation quotation = createQuotation(customerId, QuotationStatus.AWAITING_APPROVAL);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        this.decideQuotationByCustomerAppService.execute(
                DecideQuotationByCustomerCommand.of(quotation.getId(), QuotationDecision.REJECT, authenticatedUser, "Muito caro"));

        verify(this.decideQuotationUseCase).execute(DecideQuotationCommand.builder()
                                                            .quotationId(quotation.getId())
                                                            .decision(QuotationDecision.REJECT)
                                                            .reason("Muito caro")
                                                            .channel(QuotationDecisionChannel.CUSTOMER_ACCOUNT)
                                                            .decidedBy(authenticatedUser.id())
                                                            .build());
    }

    @Test
    @DisplayName("Não deve decidir orçamento de outro cliente")
    void shouldNotDecideQuotationOfAnotherCustomer() {
        Quotation quotation = createQuotation();
        var authenticatedUser = UserTestFactory.authenticatedUserCustomer(CustomerId.generate());

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotFoundException.class, () -> this.decideQuotationByCustomerAppService.execute(
                DecideQuotationByCustomerCommand.of(quotation.getId(), QuotationDecision.APPROVE, authenticatedUser, null)));

        verifyNoInteractions(this.decideQuotationUseCase);
    }
}
