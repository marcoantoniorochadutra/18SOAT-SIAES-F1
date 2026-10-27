package com.fiap.siaes.user.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.user.application.usecase.DeleteCustomerUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDeletedSubscriber {

    private final DeleteCustomerUsersUseCase deleteCustomerUsersUseCase;

    @EventListener
    @Order(4)
    public void on(CustomerDeletedEvent event) {
        this.deleteCustomerUsersUseCase.execute(event.customerId());
    }
}
