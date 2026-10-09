package com.fiap.siaes.customer.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface UpdateCustomerUseCase {

    CustomerResponse execute(UpdateCustomerCommand command);

    @Builder(toBuilder = true)
    public record UpdateCustomerCommand(
            @JsonIgnore CustomerId id,
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotBlank(message = "{CustomerCommand.document.notBlank}")
            String document,

            @NotBlank(message = "{CustomerCommand.name.notBlank}")
            @Size(message = "{CustomerCommand.name.size.max}", max = 150)
            String name,

            @Size(message = "{CustomerCommand.phone.size.max}", max = 20)
            String phone,

            @Email(message = "{CustomerCommand.email.email}")
            @Size(message = "{CustomerCommand.email.size.max}", max = 150)
            String email) {

        public UpdateCustomerCommand with(CustomerId id, AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .id(id)
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
