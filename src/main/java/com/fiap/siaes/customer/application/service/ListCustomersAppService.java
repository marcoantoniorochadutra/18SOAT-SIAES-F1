package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCustomersAppService implements ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> execute() {
        return this.customerRepository.findAll();
    }
}
