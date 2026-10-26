package com.fiap.siaes.supplies.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public interface UpdateSuppliesUseCase {

    SuppliesId execute(UpdateSuppliesCommand command);

    record UpdateSuppliesCommand(
            @JsonIgnore SuppliesId id,

            @NotBlank(message = "{SuppliesCommand.sku.notBlank}")
            @Size(message = "{SuppliesCommand.sku.size.max}", max = 50)
            String sku,

            @NotBlank(message = "{SuppliesCommand.description.notBlank}")
            @Size(message = "{SuppliesCommand.description.size.max}", max = 150)
            String description,

            @NotNull(message = "{SuppliesCommand.unitPrice.notNull}")
            @PositiveOrZero(message = "{SuppliesCommand.unitPrice.positiveOrZero}")
            @Digits(message = "{SuppliesCommand.unitPrice.digits}", integer = 10, fraction = 2)
            BigDecimal unitPrice) {

        public static UpdateSuppliesCommand of(SuppliesId id, String sku, String description, BigDecimal unitPrice) {
            return new UpdateSuppliesCommand(id, sku, description, unitPrice);
        }

        public UpdateSuppliesCommand with(SuppliesId id) {
            return of(id, this.sku, this.description, this.unitPrice);
        }
    }
}
