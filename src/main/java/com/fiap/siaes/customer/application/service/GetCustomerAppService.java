package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCustomerAppService implements GetCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Customer execute(CustomerId id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
    }
}
