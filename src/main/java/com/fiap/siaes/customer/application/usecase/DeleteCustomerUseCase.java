package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.CustomerId;

public interface DeleteCustomerUseCase {

    void execute(CustomerId id);
}
