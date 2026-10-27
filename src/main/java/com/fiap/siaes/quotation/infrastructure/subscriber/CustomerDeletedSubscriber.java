package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase.DeleteCustomerQuotationsCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDeletedSubscriber {

    private final DeleteCustomerQuotationsUseCase deleteCustomerQuotationsUseCase;

    @EventListener
    @Order(1)
    public void on(CustomerDeletedEvent event) {
        this.deleteCustomerQuotationsUseCase.execute(DeleteCustomerQuotationsCommand.of(event.customerId(), event.deletedBy()));
    }
}
