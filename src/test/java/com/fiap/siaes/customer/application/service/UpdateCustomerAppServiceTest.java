package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Customer")
class UpdateCustomerAppServiceTest {

    private static final AuthenticatedUser MANAGER =
            new AuthenticatedUser(UserId.generate(), null, "manager@email.com", UserRole.MANAGER);

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
                .authenticatedUser(MANAGER)
                .name("Maria Atualizada")
                .phone("11988887777")
                .email("maria.atualizada@email.com")
                .document("529.982.247-25")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        CustomerResponse response = this.updateCustomerAppService.execute(command);

        assertEquals(customer.getId(), response.id());
        assertEquals("Maria Atualizada", response.name());
        assertEquals("11988887777", response.phone());
        assertEquals("maria.atualizada@email.com", response.email());
        assertEquals("52998224725", response.document());
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

        CustomerResponse response = this.updateCustomerAppService.execute(command);

        assertEquals("Maria Atualizada", response.name());
        verify(this.customerRepository).save(customer);
    }

    @Test
    @DisplayName("Não deve considerar o id da requisição quando o usuário autenticado é CUSTOMER")
    void shouldIgnoreRequestedIdWhenAuthenticatedUserIsCustomer() {
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

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        this.updateCustomerAppService.execute(command);

        verify(this.customerRepository, never()).findByIdOrThrowNotFound(requestedId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente a ser atualizado não existir")
    void shouldThrowWhenCustomerToUpdateDoesNotExist() {
        var id = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .id(id)
                .authenticatedUser(MANAGER)
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
    @DisplayName("Não deve atualizar quando o e-mail já pertencer a outro cliente")
    void shouldNotUpdateWhenEmailAlreadyBelongsToAnotherCustomer() {
        var customer = createCustomer();
        var command = UpdateCustomerCommand.builder()
                .id(customer.getId())
                .authenticatedUser(MANAGER)
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
                .authenticatedUser(MANAGER)
                .name("Maria Silva")
                .email("maria@email.com")
                .document("52998224725")
                .build();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);
        when(this.customerRepository.existsByDocumentAndIdNot("52998224725", customer.getId())).thenReturn(true);

        var exception = assertThrows(CustomerDocumentAlreadyExistsException.class,
                () -> this.updateCustomerAppService.execute(command));

        assertExceptionParameters(exception, "52998224725");
        verify(this.customerRepository, never()).save(any());
    }
}
