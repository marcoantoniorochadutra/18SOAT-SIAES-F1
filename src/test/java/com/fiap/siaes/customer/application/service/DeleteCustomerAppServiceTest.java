package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase.DeleteCustomerCommand;
import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserManager;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer")
class DeleteCustomerAppServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private DeleteCustomerAppService deleteCustomerAppService;

    @Test
    @DisplayName("Deve publicar a exclusão do cliente antes de removê-lo")
    void shouldPublishCustomerDeletedBeforeDeletingCustomer() {
        var customer = createCustomer();
        var authenticatedUser = authenticatedUserManager();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        this.deleteCustomerAppService.execute(DeleteCustomerCommand.of(customer.getId(), authenticatedUser));

        InOrder inOrder = inOrder(this.domainEventPublisher, this.customerRepository);
        inOrder.verify(this.domainEventPublisher).publish(argThat(events -> events.size() == 1
                && events.getFirst() instanceof CustomerDeletedEvent event
                && customer.getId().equals(event.customerId())
                && authenticatedUser.id().equals(event.deletedBy())));
        inOrder.verify(this.customerRepository).deleteById(customer.getId());
    }

    @Test
    @DisplayName("Não deve remover cliente inexistente")
    void shouldNotDeleteCustomerWhenNotFound() {
        var id = CustomerId.generate();
        var command = DeleteCustomerCommand.of(id, authenticatedUserManager());

        when(this.customerRepository.findByIdOrThrowNotFound(id)).thenThrow(new CustomerNotFoundException(id.toString()));

        var exception = assertThrows(CustomerNotFoundException.class, () -> this.deleteCustomerAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.customerRepository, never()).deleteById(any());
        verifyNoInteractions(this.domainEventPublisher);
    }
}
