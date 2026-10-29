package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.workorder.application.usecase.DeleteCustomerWorkOrdersUseCase;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Customer Deleted (Work Order)")
class WorkOrderCustomerDeletedSubscriberTest {

    @Mock
    private DeleteCustomerWorkOrdersUseCase deleteCustomerWorkOrdersUseCase;

    @InjectMocks
    private WorkOrderCustomerDeletedSubscriber customerDeletedSubscriber;

    @Test
    @DisplayName("Deve remover as ordens de serviço do cliente excluído")
    void shouldDeleteCustomerWorkOrders() {
        var event = CustomerDeletedEvent.of(CustomerId.generate(), UserId.generate(), Instant.now());

        this.customerDeletedSubscriber.on(event);

        verify(this.deleteCustomerWorkOrdersUseCase).execute(event.customerId());
    }
}
