package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DecideQuotationByCustomerAppService implements DecideQuotationByCustomerUseCase {

    private final QuotationRepository quotationRepository;
    private final DecideQuotationUseCase decideQuotationUseCase;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DecideQuotationByCustomerCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.id());

        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        if (authenticatedUser.isDifferentCustomer(quotation.getCustomerId()))
            throw new QuotationNotFoundException(command.id().toString());

        this.decideQuotationUseCase.execute(DecideQuotationCommand.builder()
                                                    .quotationId(quotation.getId())
                                                    .decision(command.decision())
                                                    .reason(command.reason())
                                                    .channel(QuotationDecisionChannel.CUSTOMER_ACCOUNT)
                                                    .decidedBy(authenticatedUser.id())
                                                    .build());
    }
}
