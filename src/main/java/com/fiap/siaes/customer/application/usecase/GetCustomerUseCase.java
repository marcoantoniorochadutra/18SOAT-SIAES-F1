package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface GetCustomerUseCase {

    CustomerResponse execute(GetCustomerByIdCommand id);

    record GetCustomerByIdCommand(CustomerId id, AuthenticatedUser authenticatedUser) {
        public static GetCustomerByIdCommand from(CustomerId id, AuthenticatedUser authenticatedUser) {
            return new GetCustomerByIdCommand(id, authenticatedUser);
        }
    }
}
