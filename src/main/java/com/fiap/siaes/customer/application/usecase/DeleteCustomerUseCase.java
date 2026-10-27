package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface DeleteCustomerUseCase {

    void execute(DeleteCustomerCommand command);

    record DeleteCustomerCommand(CustomerId id, AuthenticatedUser authenticatedUser) {

        public static DeleteCustomerCommand of(CustomerId id, AuthenticatedUser authenticatedUser) {
            return new DeleteCustomerCommand(id, authenticatedUser);
        }
    }
}
