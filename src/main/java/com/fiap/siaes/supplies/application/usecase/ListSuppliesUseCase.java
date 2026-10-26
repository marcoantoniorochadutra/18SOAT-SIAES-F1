package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;

public interface ListSuppliesUseCase {

    PageResponse<SuppliesResponse> execute(ListSuppliesCommand command);

    record ListSuppliesCommand(SuppliesFilter filter, PageQuery pageQuery) {
        public static ListSuppliesCommand of(SuppliesFilter filter, PageQuery pageQuery) {
            return new ListSuppliesCommand(filter, pageQuery);
        }
    }
}
