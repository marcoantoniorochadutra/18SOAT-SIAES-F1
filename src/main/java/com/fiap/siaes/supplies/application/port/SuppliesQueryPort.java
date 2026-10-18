package com.fiap.siaes.supplies.application.port;

import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface SuppliesQueryPort {

    PageResponse<SuppliesResponse> findAllByFilter(SuppliesFilter filter, PageQuery pageQuery);
}
