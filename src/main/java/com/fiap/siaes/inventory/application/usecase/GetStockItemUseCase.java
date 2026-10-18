package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface GetStockItemUseCase {

    StockItemResponse execute(SuppliesId suppliesId);
}
