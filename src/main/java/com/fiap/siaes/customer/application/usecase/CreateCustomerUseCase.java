package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface CreateCustomerUseCase {

    CustomerId execute(CreateCustomerCommand command);

    @Builder
    public record CreateCustomerCommand(
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

    }
}
