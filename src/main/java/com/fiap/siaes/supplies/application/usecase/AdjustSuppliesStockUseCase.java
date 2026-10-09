package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface AdjustSuppliesStockUseCase {

    Supplies execute(SuppliesId id, AdjustSuppliesStockCommand command);

    record AdjustSuppliesStockCommand(int quantity) {}
}
