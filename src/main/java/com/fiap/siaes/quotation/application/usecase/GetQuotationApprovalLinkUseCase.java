package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;

public interface GetQuotationApprovalLinkUseCase {

    QuotationApprovalLinkResponse execute(GetQuotationApprovalLinkCommand command);

    record GetQuotationApprovalLinkCommand(QuotationId quotationId) {

        public static GetQuotationApprovalLinkCommand of(QuotationId quotationId) {
            return new GetQuotationApprovalLinkCommand(quotationId);
        }
    }
}
