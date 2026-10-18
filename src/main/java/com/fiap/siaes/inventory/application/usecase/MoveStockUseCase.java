package com.fiap.siaes.inventory.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface MoveStockUseCase {

    SuppliesId execute(MoveStockCommand command);

    @Builder(toBuilder = true)
    record MoveStockCommand(
            @JsonIgnore SuppliesId suppliesId,
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotNull(message = "{MoveStockCommand.type.notNull}")
            StockMovementType type,

            int quantity,

            @Size(message = "{MoveStockCommand.reason.size.max}", max = 255)
            String reason) {

        public MoveStockCommand with(SuppliesId suppliesId, AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .suppliesId(suppliesId)
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
