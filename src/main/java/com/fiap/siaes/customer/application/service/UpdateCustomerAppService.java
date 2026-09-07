package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCustomerAppService implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer execute(CustomerId id, UpdateCustomerCommand command) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        customer.setName(command.name());
        customer.setPhone(command.phone());
        customer.setEmail(command.email());

        return this.customerRepository.save(customer);
    }
}
