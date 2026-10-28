package com.fiap.siaes.user.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface DeleteCustomerUsersUseCase {

    void execute(CustomerId customerId);
}
