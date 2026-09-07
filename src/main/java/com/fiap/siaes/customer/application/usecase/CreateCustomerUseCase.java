package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.CustomerId;

public interface CreateCustomerUseCase {

    CustomerId execute(CreateCustomerCommand command);

    record CreateCustomerCommand(String document, String name, String phone, String email) {}
}
