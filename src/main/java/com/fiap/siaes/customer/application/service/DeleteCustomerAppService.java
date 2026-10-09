package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerAppService implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void execute(CustomerId id) {
        Customer customer = this.customerRepository.findByIdOrThrowNotFound(id);

        this.customerRepository.deleteById(customer.getId());
    }
}
