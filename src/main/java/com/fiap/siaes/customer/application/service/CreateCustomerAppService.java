package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerAppService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerId execute(CreateCustomerCommand command) {
        return null;
    }
}
