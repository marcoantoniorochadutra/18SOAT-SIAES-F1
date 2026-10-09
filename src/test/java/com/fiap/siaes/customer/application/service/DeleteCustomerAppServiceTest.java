package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer")
class DeleteCustomerAppServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DeleteCustomerAppService deleteCustomerAppService;

    @Test
    @DisplayName("Deve remover um cliente com sucesso")
    void shouldDeleteCustomerSuccessfully() {
        var customer = createCustomer();

        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        this.deleteCustomerAppService.execute(customer.getId());

        verify(this.customerRepository).deleteById(customer.getId());
    }

    @Test
    @DisplayName("Não deve remover cliente inexistente")
    void shouldNotDeleteCustomerWhenNotFound() {
        var id = CustomerId.generate();

        when(this.customerRepository.findByIdOrThrowNotFound(id)).thenThrow(new CustomerNotFoundException(id.toString()));

        var exception = assertThrows(CustomerNotFoundException.class, () -> this.deleteCustomerAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
        verify(this.customerRepository, never()).deleteById(any());
    }
}
