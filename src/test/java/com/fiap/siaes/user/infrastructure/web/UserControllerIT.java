package com.fiap.siaes.user.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - User")
class UserControllerIT extends IntegrationTestUtils {

    private static final String EMAIL = "joao@siaes.com";

    @Test
    @DisplayName("[201] Gerente deve criar mecânico que consegue autenticar")
    void shouldCreateMechanicThatCanLogin() throws Exception {
        var managerToken = super.loginAs(UserRole.MANAGER);

        var id = super.createdId(super.executePost(UserController.ENDPOINT, managerToken, this.command(UserRole.MECHANIC, null)));

        assertEquals(UserRole.MECHANIC, super.userRepository.findById(new UserId(id)).orElseThrow().getRole());
        super.login(EMAIL, PASSWORD, "$.token");
    }

    @Test
    @DisplayName("[201] Deve criar usuário cliente vinculado ao cliente")
    void shouldCreateCustomerUser() throws Exception {
        var managerToken = super.loginAs(UserRole.MANAGER);
        var customer = persist(super.customerRepository, CustomerTestFactory.createCustomer());

        var id = super.createdId(super.executePost(UserController.ENDPOINT, managerToken, this.command(UserRole.CUSTOMER, customer.getId())));

        var user = super.userRepository.findById(new UserId(id)).orElseThrow();
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(customer.getId(), user.getCustomerId());
    }

    @Test
    @DisplayName("[403] Gerente não deve criar administrador")
    void shouldNotCreateAdminAsManager() throws Exception {
        var managerToken = super.loginAs(UserRole.MANAGER);

        super.executePost(UserController.ENDPOINT, managerToken, this.command(UserRole.ADMIN, null))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[403] Mecânico não deve criar usuário")
    void shouldNotCreateUserAsMechanic() throws Exception {
        var mechanicToken = super.loginAs(UserRole.MECHANIC);

        super.executePost(UserController.ENDPOINT, mechanicToken, this.command(UserRole.MECHANIC, null))
                .andExpect(status().isForbidden());
    }

    private CreateUserCommand command(UserRole role, CustomerId customerId) {
        return CreateUserCommand.builder()
                .name("João Souza")
                .email(EMAIL)
                .password(PASSWORD)
                .role(role)
                .customerId(customerId)
                .build();
    }
}
