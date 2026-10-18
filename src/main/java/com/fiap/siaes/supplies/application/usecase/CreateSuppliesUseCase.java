package com.fiap.siaes.supplies.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

public interface CreateSuppliesUseCase {

    SuppliesId execute(CreateSuppliesCommand command);

    @Builder(toBuilder = true)
    record CreateSuppliesCommand(
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotBlank(message = "{SuppliesCommand.sku.notBlank}")
            @Size(message = "{SuppliesCommand.sku.size.max}", max = 50)
            String sku,

            @NotBlank(message = "{SuppliesCommand.description.notBlank}")
            @Size(message = "{SuppliesCommand.description.size.max}", max = 150)
            String description,

            @NotNull(message = "{SuppliesCommand.unitPrice.notNull}")
            @PositiveOrZero(message = "{SuppliesCommand.unitPrice.positiveOrZero}")
            @Digits(message = "{SuppliesCommand.unitPrice.digits}", integer = 10, fraction = 2)
            BigDecimal unitPrice,

            @PositiveOrZero(message = "{SuppliesCommand.initialStock.positiveOrZero}")
            int initialStock) {

        public CreateSuppliesCommand with(AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
