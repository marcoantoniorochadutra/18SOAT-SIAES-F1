package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface ListStockMovementsUseCase {

    PageResponse<StockMovementResponse> execute(ListStockMovementsCommand command);

    record ListStockMovementsCommand(SuppliesId suppliesId, PageQuery pageQuery) {
        public static ListStockMovementsCommand of(SuppliesId suppliesId, PageQuery pageQuery) {
            return new ListStockMovementsCommand(suppliesId, pageQuery);
        }
    }
}
