package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCustomerAppService implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CustomerResponse execute(UpdateCustomerCommand command) {
        Customer customer = this.findCustomerToUpdate(command);

        this.validateCustomer(command);

        customer.update(command.name(), command.phone(), command.email(), command.document());

        this.customerRepository.save(customer);

        return CustomerResponse.from(customer);
    }

    private Customer findCustomerToUpdate(UpdateCustomerCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.userRole().isCustomer()) {
            return this.customerRepository.findByIdOrThrowNotFound(authenticatedUser.customerId());
        }

        return this.customerRepository.findByIdOrThrowNotFound(command.id());
    }

    private void validateCustomer(UpdateCustomerCommand command) {
        if (this.customerRepository.existsByEmailAndIdNot(command.email(), command.id())) {
            throw new CustomerEmailAlreadyExistsException(command.email());
        }

        if (this.customerRepository.existsByDocumentAndIdNot(command.document(), command.id())) {
            throw new CustomerDocumentAlreadyExistsException(command.document());
        }
    }
}
