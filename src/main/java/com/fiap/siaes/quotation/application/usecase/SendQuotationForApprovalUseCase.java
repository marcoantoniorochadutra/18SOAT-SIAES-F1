package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.domain.model.vo.QuotationId;

public interface SendQuotationForApprovalUseCase {

    void execute(SendQuotationForApprovalCommand command);

    record SendQuotationForApprovalCommand(QuotationId quotationId) {

        public static SendQuotationForApprovalCommand of(QuotationId quotationId) {
            return new SendQuotationForApprovalCommand(quotationId);
        }
    }
}
