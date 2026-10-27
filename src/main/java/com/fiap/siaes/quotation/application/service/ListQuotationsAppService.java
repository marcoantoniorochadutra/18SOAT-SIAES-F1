package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.port.QuotationQueryPort;
import com.fiap.siaes.quotation.application.usecase.ListQuotationsUseCase;
import com.fiap.siaes.sk.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListQuotationsAppService implements ListQuotationsUseCase {

    private final QuotationQueryPort quotationQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<ListQuotationResponse> execute(ListQuotationsCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        QuotationFilter filter = authenticatedUser.isCustomer() ? command.filter().withCustomerId(authenticatedUser.customerId()) : command.filter();

        return this.quotationQueryPort.findAllByFilter(filter, !authenticatedUser.isCustomer(), command.pageQuery());
    }
}
