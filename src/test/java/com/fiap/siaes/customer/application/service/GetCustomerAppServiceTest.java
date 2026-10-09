package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase.GetCustomerByIdCommand;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Customer")
class GetCustomerAppServiceTest {

    private static final AuthenticatedUser AUTHENTICATED_USER =
            new AuthenticatedUser(UserId.generate(), null, "maria@email.com", UserRole.MANAGER);

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private GetCustomerAppService getCustomerAppService;

    @Test
    @DisplayName("Deve buscar um cliente pelo id com sucesso")
    void shouldGetCustomerSuccessfully() {
        var customer = createCustomer();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var command = GetCustomerByIdCommand.from(customer.getId(), AUTHENTICATED_USER);
        CustomerResponse response = this.getCustomerAppService.execute(command);

        assertEquals(customer.getId(), response.id());
        assertEquals(customer.getDocument().getValue(), response.document());
        assertEquals(DocumentType.CPF, response.documentType());
        assertEquals(customer.getName(), response.name());
        assertEquals(customer.getPhone(), response.phone());
        assertEquals(customer.getEmail(), response.email());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não existir")
    void shouldThrowWhenCustomerNotFound() {
        var id = CustomerId.generate();
        when(this.customerRepository.findByIdOrThrowNotFound(id)).thenThrow(new CustomerNotFoundException(id.toString()));

        var command = GetCustomerByIdCommand.from(id, AUTHENTICATED_USER);
        var exception = assertThrows(CustomerNotFoundException.class, () -> this.getCustomerAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve buscar o próprio cliente vinculado quando o usuário autenticado é CUSTOMER")
    void shouldGetOwnLinkedCustomerWhenAuthenticatedUserIsCustomer() {
        var customer = createCustomer();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(customer.getId());

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var command = GetCustomerByIdCommand.from(CustomerId.generate(), authenticatedCustomer);
        CustomerResponse response = this.getCustomerAppService.execute(command);

        assertEquals(customer.getId(), response.id());
        assertEquals(customer.getEmail(), response.email());
    }

    @Test
    @DisplayName("Não deve considerar o id da requisição quando o usuário autenticado é CUSTOMER")
    void shouldIgnoreRequestedIdWhenAuthenticatedUserIsCustomer() {
        var customer = createCustomer();
        var requestedId = CustomerId.generate();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(customer.getId());

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var command = GetCustomerByIdCommand.from(requestedId, authenticatedCustomer);
        this.getCustomerAppService.execute(command);

        verify(this.customerRepository, never()).findByIdOrThrowNotFound(requestedId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente vinculado ao usuário CUSTOMER não existir")
    void shouldThrowWhenLinkedCustomerDoesNotExist() {
        var customerId = CustomerId.generate();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(customerId);

        when(this.customerRepository.findByIdOrThrowNotFound(customerId))
                .thenThrow(new CustomerNotFoundException(customerId.toString()));

        var command = GetCustomerByIdCommand.from(CustomerId.generate(), authenticatedCustomer);

        assertThrows(CustomerNotFoundException.class, () -> this.getCustomerAppService.execute(command));
    }
}
