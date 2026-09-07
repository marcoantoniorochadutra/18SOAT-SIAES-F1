package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;

public interface GetCustomerUseCase {

    Customer execute(CustomerId id);
}
