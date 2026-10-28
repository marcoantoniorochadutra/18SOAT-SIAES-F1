package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.workorder.application.usecase.DeleteCustomerWorkOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDeletedSubscriber {

    private final DeleteCustomerWorkOrdersUseCase deleteCustomerWorkOrdersUseCase;

    @EventListener
    @Order(2)
    public void on(CustomerDeletedEvent event) {
        this.deleteCustomerWorkOrdersUseCase.execute(event.customerId());
    }
}
