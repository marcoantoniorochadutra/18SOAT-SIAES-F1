package com.fiap.siaes.quotation.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationMaintenanceItemCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public interface UpdateQuotationUseCase {

    QuotationId execute(UpdateQuotationCommand command);

    @Builder(toBuilder = true)
    record UpdateQuotationCommand(
            @JsonIgnore QuotationId id,
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @Size(message = "{QuotationCommand.description.size.max}", max = 500)
            String description,

            @NotNull(message = "{QuotationCommand.odometer.notNull}")
            @PositiveOrZero(message = "{QuotationCommand.odometer.positiveOrZero}")
            Long odometer,

            List<@Valid @NotNull(message = "{QuotationCommand.item.notNull}") QuotationSuppliesItemCommand> supplies,

            List<@Valid @NotNull(message = "{QuotationCommand.item.notNull}") QuotationMaintenanceItemCommand> maintenances) {

        public UpdateQuotationCommand with(QuotationId id, AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .id(id)
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
