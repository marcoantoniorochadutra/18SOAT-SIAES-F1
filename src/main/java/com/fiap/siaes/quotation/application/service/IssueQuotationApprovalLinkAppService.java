package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.IssueQuotationApprovalLinkUseCase;
import com.fiap.siaes.quotation.domain.exception.QuotationNotAwaitingApprovalException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueQuotationApprovalLinkAppService implements IssueQuotationApprovalLinkUseCase {

    private final QuotationRepository quotationRepository;
    private final QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public QuotationApprovalLinkResponse execute(IssueQuotationApprovalLinkCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.quotationId());

        if (quotation.getLastStatus().isNotAwaitingApproval())
            throw new QuotationNotAwaitingApprovalException(quotation.getId().toString(), quotation.getLastStatus().name());

        QuotationApprovalToken token = this.quotationApprovalTokenHelper.currentOrIssue(quotation.getId());

        return QuotationApprovalLinkResponse.of(this.quotationApprovalTokenHelper.linkFor(token), token.getExpiresAt());
    }
}
