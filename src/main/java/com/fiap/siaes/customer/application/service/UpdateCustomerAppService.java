package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
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
public class UpdateCustomerAppService implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CustomerId execute(UpdateCustomerCommand command) {
        Customer customer = this.findCustomerToUpdate(command);

        this.validateCustomer(command);

        customer.update(command.name(), command.phone(), command.email(), command.document());

        this.customerRepository.save(customer);

        return customer.getId();
    }

    private Customer findCustomerToUpdate(UpdateCustomerCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer()) {
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
