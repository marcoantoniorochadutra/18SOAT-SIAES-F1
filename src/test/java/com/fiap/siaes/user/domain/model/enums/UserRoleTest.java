package com.fiap.siaes.user.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - UserRole")
class UserRoleTest {

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Deve resolver o UserRole a partir do seu ordinal")
    void shouldResolveRoleFromOrdinal(UserRole role) {
        assertEquals(UserRole.fromOrdinal(role.ordinal()), role);
    }

    @Test
    @DisplayName("Deve retornar nulo quando o ordinal não corresponde a nenhum perfil")
    void shouldReturnNullWhenOrdinalDoesNotMatchAnyRole() {
        assertNull(UserRole.fromOrdinal(99));
    }

    @ParameterizedTest
    @MethodSource("rolesWithoutLessAccess")
    @DisplayName("Não deve carecer de acesso a um perfil de hierarquia igual ou inferior")
    void shouldNotLackAccessToEqualOrLowerRole(UserRole current, UserRole required) {
        assertFalse(current.lacksAccessTo(required));
    }

    @ParameterizedTest
    @MethodSource("rolesWithLessAccess")
    @DisplayName("Deve carecer de acesso a um perfil de hierarquia superior")
    void shouldLackAccessToHigherRole(UserRole current, UserRole required) {
        assertTrue(current.lacksAccessTo(required));
    }

    @Test
    @DisplayName("Deve identificar um usuário com perfil CUSTOMER")
    void shouldIdentifyCustomerRole() {
        assertTrue(UserRole.CUSTOMER.isCustomer());
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = "CUSTOMER", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve identificar como CUSTOMER perfis diferentes")
    void shouldNotIdentifyOtherRolesAsCustomer(UserRole role) {
        assertFalse(role.isCustomer());
    }

    static Stream<Arguments> rolesWithoutLessAccess() {
        return Stream.of(
                Arguments.of(UserRole.ADMIN, UserRole.MANAGER),
                Arguments.of(UserRole.MANAGER, UserRole.MANAGER),
                Arguments.of(UserRole.MECHANIC, UserRole.CUSTOMER),
                Arguments.of(UserRole.CUSTOMER, UserRole.MECHANIC)
        );
    }

    static Stream<Arguments> rolesWithLessAccess() {
        return Stream.of(
                Arguments.of(UserRole.MANAGER, UserRole.ADMIN),
                Arguments.of(UserRole.CUSTOMER, UserRole.MANAGER)
        );
    }
}
