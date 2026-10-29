package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase.DeleteCustomerQuotationsCommand;
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
@DisplayName("UnitTest - Subscriber - Customer Deleted (Quotation)")
class QuotationCustomerDeletedSubscriberTest {

    @Mock
    private DeleteCustomerQuotationsUseCase deleteCustomerQuotationsUseCase;

    @InjectMocks
    private QuotationCustomerDeletedSubscriber customerDeletedSubscriber;

    @Test
    @DisplayName("Deve remover os orçamentos do cliente excluído")
    void shouldDeleteCustomerQuotations() {
        var event = CustomerDeletedEvent.of(CustomerId.generate(), UserId.generate(), Instant.now());

        this.customerDeletedSubscriber.on(event);

        verify(this.deleteCustomerQuotationsUseCase).execute(DeleteCustomerQuotationsCommand.of(event.customerId(), event.deletedBy()));
    }
}
