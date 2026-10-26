package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;

import java.util.UUID;

public interface ReleaseStockReservationUseCase {

    void execute(ReleaseStockReservationCommand command);

    @Builder
    record ReleaseStockReservationCommand(SuppliesId suppliesId, int quantity, UUID referenceId, UserId performedBy,
            String performedByName) {
    }
}
