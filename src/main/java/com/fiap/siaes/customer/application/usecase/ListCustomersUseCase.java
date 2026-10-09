package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface ListCustomersUseCase {

    PageResponse<CustomerResponse> execute(ListCustomersCommand command);

    record ListCustomersCommand(CustomerFilter filter, PageQuery pageQuery) {
        public static ListCustomersCommand from(CustomerFilter filter, PageQuery pageQuery) {
            return new ListCustomersCommand(filter, pageQuery);
        }
    }
}
