package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Customer")
class UpdateCustomerAppServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private UpdateCustomerAppService updateCustomerAppService;

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void shouldUpdateCustomerSuccessfully() {
        var customer = createCustomer();
        var command = UpdateCustomerCommand.builder()
                .id(customer.getId())
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .name("Maria Atualizada")
                .phone("11988887777")
                .email("maria.atualizada@email.com")
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var id = this.updateCustomerAppService.execute(command);

        Customer savedCustomer = captureSave(this.customerRepository, Customer.class);

        assertEquals(customer.getId(), id);
        assertEquals("Maria Atualizada", savedCustomer.getName());
        assertEquals("11988887777", savedCustomer.getPhone());
        assertEquals("maria.atualizada@email.com", savedCustomer.getEmail());
        assertEquals("52998224725", savedCustomer.getDocument().getValue());

        verify(this.customerRepository).save(customer);
    }

    @Test
    @DisplayName("Deve atualizar o próprio cliente vinculado quando o usuário autenticado é CUSTOMER")
    void shouldUpdateOwnLinkedCustomerWhenAuthenticatedUserIsCustomer() {
        var customer = createCustomer();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(customer.getId());
        var command = UpdateCustomerCommand.builder()
                .id(customer.getId())
                .authenticatedUser(authenticatedCustomer)
                .name("Maria Atualizada")
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        this.updateCustomerAppService.execute(command);

        assertEquals("Maria Atualizada", customer.getName());
        verify(this.customerRepository).save(customer);
    }

    @Test
    @DisplayName("Não deve permitir que um CUSTOMER atualize outro cliente")
    void shouldNotUpdateAnotherCustomerWhenAuthenticatedUserIsCustomer() {
        var customer = createCustomer();
        var requestedId = CustomerId.generate();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(customer.getId());
        var command = UpdateCustomerCommand.builder()
                .id(requestedId)
                .authenticatedUser(authenticatedCustomer)
                .name("Maria Atualizada")
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .document("529.982.247-25")
                .build();

        var exception = assertThrows(CustomerNotFoundException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, requestedId.toString());
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente a ser atualizado não existir")
    void shouldThrowWhenCustomerToUpdateDoesNotExist() {
        var id = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .id(id)
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .name("Maria Silva")
                .email("maria@email.com")
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(id))
                .thenThrow(new CustomerNotFoundException(id.toString()));

        var exception = assertThrows(CustomerNotFoundException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente vinculado ao usuário CUSTOMER não existir")
    void shouldThrowWhenLinkedCustomerDoesNotExist() {
        var customerId = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .id(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserCustomer(customerId))
                .name("Maria Silva")
                .email("maria@email.com")
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customerId))
                .thenThrow(new CustomerNotFoundException(customerId.toString()));

        var exception = assertThrows(CustomerNotFoundException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, customerId.toString());
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando o e-mail já pertencer a outro cliente")
    void shouldNotUpdateWhenEmailAlreadyBelongsToAnotherCustomer() {
        var customer = createCustomer();
        var command = UpdateCustomerCommand.builder()
                .id(customer.getId())
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .name("Maria Silva")
                .email("outro@email.com")
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);
        when(this.customerRepository.existsByEmailAndIdNot("outro@email.com", customer.getId())).thenReturn(true);

        var exception = assertThrows(CustomerEmailAlreadyExistsException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, "outro@email.com");
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando o documento já pertencer a outro cliente")
    void shouldNotUpdateWhenDocumentAlreadyBelongsToAnotherCustomer() {
        var customer = createCustomer();
        var command = UpdateCustomerCommand.builder()
                .id(customer.getId())
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .name("Maria Silva")
                .email("maria@email.com")
                .document("52998224725")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);
        when(this.customerRepository.existsByDocumentAndIdNot(Document.create("52998224725"), customer.getId())).thenReturn(true);

        var exception = assertThrows(CustomerDocumentAlreadyExistsException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, "52998224725");
        verify(this.customerRepository, never()).save(any());
    }
}
