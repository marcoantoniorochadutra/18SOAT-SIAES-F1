package com.fiap.siaes.customer.application.usecase;

import com.fiap.siaes.customer.domain.model.Customer;

import java.util.List;

public interface ListCustomersUseCase {

    List<Customer> execute();
}
