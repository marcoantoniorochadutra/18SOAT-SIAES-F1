package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.GetCustomerByIdUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdAppService implements GetCustomerByIdUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public CustomerResponse execute(GetCustomerByIdCommand command) {
        Customer customer = this.findCustomer(command);

        return CustomerResponse.from(customer);
    }

    private Customer findCustomer(GetCustomerByIdCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();

        if (authenticatedUser.isCustomer()) {
            return this.customerRepository.findByIdOrThrowNotFound(authenticatedUser.customerId());
        }

        return this.customerRepository.findByIdOrThrowNotFound(command.id());
    }

}
