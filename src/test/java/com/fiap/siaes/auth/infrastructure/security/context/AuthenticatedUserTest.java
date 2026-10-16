package com.fiap.siaes.auth.infrastructure.security.context;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Security - Authenticated User")
class AuthenticatedUserTest {

    @Test
    @DisplayName("Deve identificar usuário com perfil CUSTOMER")
    void shouldIdentifyCustomerRole() {
        assertTrue(UserTestFactory.authenticatedUserCustomer().isCustomer());
        assertFalse(UserTestFactory.authenticatedUserManager().isCustomer());
    }

    @Test
    @DisplayName("Não deve considerar diferente quando o cliente informado é o vinculado ao usuário")
    void shouldNotBeDifferentCustomerWhenSameCustomer() {
        var customerId = CustomerId.generate();

        assertFalse(UserTestFactory.authenticatedUserCustomer(customerId).isDifferentCustomer(customerId));
    }

    @Test
    @DisplayName("Deve considerar diferente quando o cliente informado não é o vinculado ao usuário")
    void shouldBeDifferentCustomerWhenAnotherCustomer() {
        var authenticatedUser = UserTestFactory.authenticatedUserCustomer(CustomerId.generate());

        assertTrue(authenticatedUser.isDifferentCustomer(CustomerId.generate()));
    }

    @Test
    @DisplayName("Deve considerar diferente quando o usuário não possui cliente vinculado")
    void shouldBeDifferentCustomerWhenUserHasNoLinkedCustomer() {
        var authenticatedUser = UserTestFactory.authenticatedUserCustomer();

        assertTrue(authenticatedUser.isDifferentCustomer(CustomerId.generate()));
    }
}
