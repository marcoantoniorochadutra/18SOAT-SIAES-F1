package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;

public interface UpdateCustomerUseCase {

    Customer execute(CustomerId id, UpdateCustomerCommand command);

    record UpdateCustomerCommand(String name, String phone, String email) {}
}
