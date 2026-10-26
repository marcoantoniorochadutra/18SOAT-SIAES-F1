package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface ListQuotationsUseCase {

    PageResponse<ListQuotationResponse> execute(ListQuotationsCommand command);

    record ListQuotationsCommand(QuotationFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
        public static ListQuotationsCommand of(QuotationFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
            return new ListQuotationsCommand(filter, pageQuery, authenticatedUser);
        }
    }
}
