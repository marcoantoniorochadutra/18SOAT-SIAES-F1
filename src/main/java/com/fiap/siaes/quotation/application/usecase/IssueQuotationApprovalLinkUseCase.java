package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;

public interface IssueQuotationApprovalLinkUseCase {

    QuotationApprovalLinkResponse execute(IssueQuotationApprovalLinkCommand command);

    record IssueQuotationApprovalLinkCommand(QuotationId quotationId) {

        public static IssueQuotationApprovalLinkCommand of(QuotationId quotationId) {
            return new IssueQuotationApprovalLinkCommand(quotationId);
        }
    }
}
