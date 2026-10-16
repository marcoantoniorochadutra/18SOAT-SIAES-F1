package com.fiap.siaes.vehicle.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface CreateVehicleUseCase {

    VehicleId execute(CreateVehicleCommand command);

    @Builder(toBuilder = true)
    record CreateVehicleCommand(
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotNull(message = "{VehicleCommand.customerId.notNull}")
            CustomerId customerId,

            @NotBlank(message = "{VehicleCommand.plate.notBlank}")
            @Size(message = "{VehicleCommand.plate.size.max}", max = 12)
            String plate,

            @NotBlank(message = "{VehicleCommand.brand.notBlank}")
            @Size(message = "{VehicleCommand.brand.size.max}", max = 100)
            String brand,

            @NotBlank(message = "{VehicleCommand.model.notBlank}")
            @Size(message = "{VehicleCommand.model.size.max}", max = 100)
            String model,

            @Min(value = 1900, message = "{VehicleCommand.year.min}")
            int year) {

        public CreateVehicleCommand with(AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
