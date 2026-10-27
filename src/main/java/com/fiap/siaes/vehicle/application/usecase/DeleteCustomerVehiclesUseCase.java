package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public interface DeleteCustomerVehiclesUseCase {

    void execute(CustomerId customerId);
}
