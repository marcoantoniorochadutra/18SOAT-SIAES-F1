package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;

public interface CreateStockItemUseCase {

    void execute(CreateStockItemCommand command);

    record CreateStockItemCommand(SuppliesId suppliesId, int initialQuantity, UserId performedBy) {

        public static CreateStockItemCommand of(SuppliesId suppliesId, int initialQuantity, UserId performedBy) {
            return new CreateStockItemCommand(suppliesId, initialQuantity, performedBy);
        }
    }
}
