package com.fiap.siaes.user.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer Users")
class DeleteCustomerUsersAppServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteCustomerUsersAppService deleteCustomerUsersAppService;

    @Test
    @DisplayName("Deve remover todos os usuários do cliente")
    void shouldDeleteAllCustomerUsers() {
        var customerId = CustomerId.generate();

        this.deleteCustomerUsersAppService.execute(customerId);

        verify(this.userRepository).deleteAllByCustomerId(customerId);
    }
}
