package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DecideQuotationByCustomerAppService implements DecideQuotationByCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final DecideQuotationUseCase decideQuotationUseCase;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DecideQuotationByCustomerCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        Customer customer = this.customerRepository.findByIdOrThrowNotFound(authenticatedUser.customerId());

        this.decideQuotationUseCase.execute(DecideQuotationCommand.builder()
                                                    .quotationId(command.id())
                                                    .ownerId(customer.getId())
                                                    .decision(command.decision())
                                                    .reason(command.reason())
                                                    .channel(QuotationDecisionChannel.CUSTOMER_ACCOUNT)
                                                    .decidedBy(authenticatedUser.id())
                                                    .decidedByName(customer.getName())
                                                    .build());
    }
}
