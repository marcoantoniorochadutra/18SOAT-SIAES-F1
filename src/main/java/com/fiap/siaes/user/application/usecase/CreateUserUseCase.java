package com.fiap.siaes.user.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface CreateUserUseCase {

    UserId execute(CreateUserCommand command);

    @Builder(toBuilder = true)
    record CreateUserCommand(
            @Size(message = "{UserCommand.name.size.max}", max = 250)
            String name,

            @Email(message = "{UserCommand.email.email}")
            @Size(message = "{UserCommand.email.size.max}", max = 150)
            String email,

            @NotBlank(message = "{UserCommand.password.notBlank}")
            String password,

            @NotNull(message = "{UserCommand.role.notNull}")
            UserRole role,

            CustomerId customerId,

            @JsonIgnore AuthenticatedUser authenticatedUser) {

        public CreateUserCommand with(AuthenticatedUser authenticatedUser) {
            return this.toBuilder().authenticatedUser(authenticatedUser).build();
        }
    }


    record UserContact(String name, String email) {
    }
}
