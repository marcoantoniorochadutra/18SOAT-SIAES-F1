package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;

public interface GetQuotationByApprovalTokenUseCase {

    QuotationApprovalResponse execute(GetQuotationByApprovalTokenCommand command);

    record GetQuotationByApprovalTokenCommand(String token) {

        public static GetQuotationByApprovalTokenCommand of(String token) {
            return new GetQuotationByApprovalTokenCommand(token);
        }
    }
}
