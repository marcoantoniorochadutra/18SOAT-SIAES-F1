package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.util.UUID;

public interface ReserveStockUseCase {

    void execute(ReserveStockCommand command);

    record ReserveStockCommand(SuppliesId suppliesId, int quantity, UUID referenceId, UserId performedBy) {

        public static ReserveStockCommand of(SuppliesId suppliesId, int quantity, UUID referenceId,
                                             UserId performedBy) {
            return new ReserveStockCommand(suppliesId, quantity, referenceId, performedBy);
        }
    }
}
