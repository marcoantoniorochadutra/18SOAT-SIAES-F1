package com.fiap.siaes.user.application.usecase;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.UserId;

public interface CreateUserUseCase {

    UserId execute(CreateUserCommand command);

    record CreateUserCommand(String name, String email, String password, UserRole role, CustomerId customerId) {
    }
}
