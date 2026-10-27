package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
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
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerId execute(UpdateCustomerCommand command) {
        Customer customer = this.findCustomerToUpdate(command);

        customer.update(command.name(), command.phone(), command.email(), command.document());

        this.validateCustomer(customer);

        this.customerRepository.save(customer);

        return customer.getId();
    }

    private Customer findCustomerToUpdate(UpdateCustomerCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer() && authenticatedUser.isDifferentCustomer(command.id())) {
            throw new CustomerNotFoundException(command.id().toString());
        }

        return this.customerRepository.findByIdOrThrowNotFound(command.id());
    }

    private void validateCustomer(Customer customer) {
        if (this.customerRepository.existsByEmailAndIdNot(customer.getEmail(), customer.getId())) {
            throw new CustomerEmailAlreadyExistsException(customer.getEmail());
        }

        if (this.customerRepository.existsByDocumentAndIdNot(customer.getDocument(), customer.getId())) {
            throw new CustomerDocumentAlreadyExistsException(customer.getDocument().getValue());
        }
    }
}
