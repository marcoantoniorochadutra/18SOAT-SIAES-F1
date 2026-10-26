package com.fiap.siaes.quotation.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public interface CreateQuotationUseCase {

    QuotationId execute(CreateQuotationCommand command);

    @Builder(toBuilder = true)
    record CreateQuotationCommand(
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotNull(message = "{QuotationCommand.workOrderId.notNull}")
            WorkOrderId workOrderId,

            @Size(message = "{QuotationCommand.description.size.max}", max = 500)
            String description,

            @NotNull(message = "{QuotationCommand.odometer.notNull}")
            @PositiveOrZero(message = "{QuotationCommand.odometer.positiveOrZero}")
            Long odometer,

            @Valid
            @NotNull(message = "{QuotationCommand.item.notNull}")
            List<QuotationSuppliesItemCommand> supplies,

            @Valid
            @NotNull(message = "{QuotationCommand.item.notNull}")
            List<QuotationMaintenanceItemCommand> maintenances) {

        public CreateQuotationCommand with(AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .authenticatedUser(authenticatedUser)
                    .build();
        }

    }

    public record QuotationMaintenanceItemCommand(
            @NotNull(message = "{QuotationCommand.maintenanceId.notNull}")
            MaintenanceId maintenanceId,

            @NotNull(message = "{QuotationCommand.quantity.notNull}")
            @Positive(message = "{QuotationCommand.quantity.positive}")
            Integer quantity) {

        public static QuotationMaintenanceItemCommand of(MaintenanceId maintenanceId, Integer quantity) {
            return new QuotationMaintenanceItemCommand(maintenanceId, quantity);
        }
    }

    public record QuotationSuppliesItemCommand(
            @NotNull(message = "{QuotationCommand.suppliesId.notNull}")
            SuppliesId suppliesId,

            @NotNull(message = "{QuotationCommand.quantity.notNull}")
            @Positive(message = "{QuotationCommand.quantity.positive}")
            Integer quantity) {

        public static QuotationSuppliesItemCommand of(SuppliesId suppliesId, Integer quantity) {
            return new QuotationSuppliesItemCommand(suppliesId, quantity);
        }
    }
}
