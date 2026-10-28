package com.fiap.siaes.vehicle.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.vehicle.application.usecase.DeleteCustomerVehiclesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDeletedSubscriber {

    private final DeleteCustomerVehiclesUseCase deleteCustomerVehiclesUseCase;

    @EventListener
    @Order(3)
    public void on(CustomerDeletedEvent event) {
        this.deleteCustomerVehiclesUseCase.execute(event.customerId());
    }
}
