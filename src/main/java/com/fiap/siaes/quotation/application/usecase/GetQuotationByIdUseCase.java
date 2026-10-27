package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;

public interface GetQuotationByIdUseCase {

    QuotationResponse execute(GetQuotationByIdCommand command);

    record GetQuotationByIdCommand(QuotationId id, AuthenticatedUser authenticatedUser) {
        public static GetQuotationByIdCommand of(QuotationId id, AuthenticatedUser authenticatedUser) {
            return new GetQuotationByIdCommand(id, authenticatedUser);
        }
    }
}
