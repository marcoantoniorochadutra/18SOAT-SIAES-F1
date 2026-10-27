package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerAppService implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DeleteCustomerCommand command) {
        Customer customer = this.customerRepository.findByIdOrThrowNotFound(command.id());

        customer.delete(command.authenticatedUser().id());

        this.domainEventPublisher.publish(customer.pullDomainEvents());

        this.customerRepository.deleteById(customer.getId());
    }
}
