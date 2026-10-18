package com.fiap.siaes.maintenance.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

public interface UpdateMaintenanceUseCase {

    MaintenanceId execute(UpdateMaintenanceCommand command);

    @Builder(toBuilder = true)
    record UpdateMaintenanceCommand(
            @JsonIgnore MaintenanceId id,

            @NotBlank(message = "{MaintenanceCommand.code.notBlank}")
            @Size(message = "{MaintenanceCommand.code.size.max}", max = 50)
            String code,

            @NotBlank(message = "{MaintenanceCommand.description.notBlank}")
            @Size(message = "{MaintenanceCommand.description.size.max}", max = 150)
            String description,

            @NotNull(message = "{MaintenanceCommand.price.notNull}")
            @PositiveOrZero(message = "{MaintenanceCommand.price.positiveOrZero}")
            @Digits(message = "{MaintenanceCommand.price.digits}", integer = 10, fraction = 2)
            BigDecimal price,

            @NotNull(message = "{MaintenanceCommand.estimatedDuration.notNull}")
            @Positive(message = "{MaintenanceCommand.estimatedDuration.positive}")
            Integer estimatedDurationMinutes) {

        public UpdateMaintenanceCommand with(MaintenanceId id) {
            return this.toBuilder()
                    .id(id)
                    .build();
        }
    }
}
