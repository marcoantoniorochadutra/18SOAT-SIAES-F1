package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalDocumentMismatchException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.repository.QuotationApprovalTokenRepository;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DecideQuotationByTokenAppService implements DecideQuotationByTokenUseCase {

    private final QuotationApprovalTokenHelper quotationApprovalTokenHelper;
    private final QuotationApprovalTokenRepository quotationApprovalTokenRepository;
    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final DecideQuotationUseCase decideQuotationUseCase;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = QuotationApprovalDocumentMismatchException.class)
    public void execute(DecideQuotationByTokenCommand command) {
        QuotationApprovalToken token = this.quotationApprovalTokenHelper.findActive(command.token());
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(token.getQuotationId());
        Customer customer = this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId());

        if (!customer.getDocument().endsWith(command.documentLastDigits())) {
            token.registerFailedAttempt();
            this.quotationApprovalTokenRepository.save(token);
            throw new QuotationApprovalDocumentMismatchException();
        }

        token.use();
        this.quotationApprovalTokenRepository.save(token);

        this.decideQuotationUseCase.execute(DecideQuotationCommand.builder()
                                                    .quotationId(quotation.getId())
                                                    .decision(command.decision())
                                                    .reason(command.reason())
                                                    .channel(QuotationDecisionChannel.APPROVAL_LINK)
                                                    .decidedByName(customer.getName())
                                                    .build());
    }
}
