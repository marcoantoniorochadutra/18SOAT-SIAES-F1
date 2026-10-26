package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.quotation.domain.model.vo.QuotationId;

public interface CompleteQuotationUseCase {

    void execute(CompleteQuotationCommand command);

    record CompleteQuotationCommand(QuotationId id) {

        public static CompleteQuotationCommand of(QuotationId id) {
            return new CompleteQuotationCommand(id);
        }
    }
}
