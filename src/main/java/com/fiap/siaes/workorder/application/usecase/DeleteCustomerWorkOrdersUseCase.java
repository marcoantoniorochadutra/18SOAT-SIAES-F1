package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface DeleteCustomerWorkOrdersUseCase {

    void execute(CustomerId customerId);
}
