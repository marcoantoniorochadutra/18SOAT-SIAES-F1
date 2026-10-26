package com.fiap.siaes.quotation.application.port;

import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface QuotationQueryPort {

    PageResponse<ListQuotationResponse> findAllByFilter(QuotationFilter filter, boolean includeDrafts, PageQuery pageQuery);
}
