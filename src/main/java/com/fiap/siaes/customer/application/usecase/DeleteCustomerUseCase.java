package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface DeleteCustomerUseCase {

    void execute(CustomerId id);
}
