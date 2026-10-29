package com.fiap.siaes.user.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.application.usecase.DeleteCustomerUsersUseCase;
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
@DisplayName("UnitTest - Subscriber - Customer Deleted (User)")
class UserCustomerDeletedSubscriberTest {

    @Mock
    private DeleteCustomerUsersUseCase deleteCustomerUsersUseCase;

    @InjectMocks
    private UserCustomerDeletedSubscriber customerDeletedSubscriber;

    @Test
    @DisplayName("Deve remover os usuários do cliente excluído")
    void shouldDeleteCustomerUsers() {
        var event = CustomerDeletedEvent.of(CustomerId.generate(), UserId.generate(), Instant.now());

        this.customerDeletedSubscriber.on(event);

        verify(this.deleteCustomerUsersUseCase).execute(event.customerId());
    }
}
