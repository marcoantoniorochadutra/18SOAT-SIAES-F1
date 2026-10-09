package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerAppService implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CustomerId execute(CreateCustomerCommand command) {
        Customer customer = this.createCustomer(command);

        this.validateCustomer(customer);

        this.customerRepository.save(customer);

        return customer.getId();
    }

    private void validateCustomer(Customer customer) {
        if (this.customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerEmailAlreadyExistsException(customer.getEmail());
        }

        if (this.customerRepository.existsByDocument(customer.getDocument())) {
            throw new CustomerDocumentAlreadyExistsException(customer.getDocument().getValue());
        }
    }

    private Customer createCustomer(CreateCustomerCommand command) {
        return Customer.builder()
                .name(command.name())
                .email(command.email())
                .phone(command.phone())
                .document(command.document())
                .build();
    }
}
