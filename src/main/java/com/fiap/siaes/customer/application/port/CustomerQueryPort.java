package com.fiap.siaes.customer.application.port;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface CustomerQueryPort {

    PageResponse<CustomerResponse> findAllByFilter(CustomerFilter filter, PageQuery pageQuery);
}
