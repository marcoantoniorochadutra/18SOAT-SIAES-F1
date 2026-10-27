package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Decide Quotation By Customer")
class DecideQuotationByCustomerAppServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DecideQuotationUseCase decideQuotationUseCase;

    @InjectMocks
    private DecideQuotationByCustomerAppService decideQuotationByCustomerAppService;

    @Test
    @DisplayName("Deve decidir o orçamento exigindo que ele pertença ao cliente e registrando o nome do cliente")
    void shouldDecideOwnQuotation() {
        Customer customer = createCustomer();
        var authenticatedUser = UserTestFactory.authenticatedUserCustomer(customer.getId());
        var quotationId = QuotationId.generate();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        this.decideQuotationByCustomerAppService.execute(
                DecideQuotationByCustomerCommand.of(quotationId, QuotationDecision.REJECT, authenticatedUser, "Muito caro"));

        verify(this.decideQuotationUseCase).execute(DecideQuotationCommand.builder()
                                                            .quotationId(quotationId)
                                                            .ownerId(customer.getId())
                                                            .decision(QuotationDecision.REJECT)
                                                            .reason("Muito caro")
                                                            .channel(QuotationDecisionChannel.CUSTOMER_ACCOUNT)
                                                            .decidedBy(authenticatedUser.id())
                                                            .decidedByName(customer.getName())
                                                            .build());
    }
}
