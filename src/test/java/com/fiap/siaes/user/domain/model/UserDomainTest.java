package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.exception.UserCustomerRelationRequiredException;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static com.fiap.siaes.user.UserTestFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - User")
class UserDomainTest {

    @Test
    @DisplayName("Deve criar um novo usuário com status ACTIVE pendente de ativação")
    void shouldCreateUserStartingAsInactive() {
        User user = User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.MANAGER)
                .build();

        assertEquals(UserStatus.ACTIVE, user.getLastStatus());
        assertEquals(1, user.getStatusHistory().size());
        UserStatusHistory history = user.getStatusHistory().iterator().next();
        assertNotNull(history.getId());
        assertEquals(UserStatus.ACTIVE, history.getStatus());
        assertNotNull(history.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve gerar um novo id quando nenhum id é informado")
    void shouldGenerateIdWhenNoneIsProvided() {
        User user = User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.MANAGER)
                .build();

        assertNotNull(user.getId());
    }

    @Test
    @DisplayName("Deve manter o id informado ao criar um usuário")
    void shouldKeepProvidedId() {
        UserId id = UserId.generate();

        User user = User.builder()
                .id(id)
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.MANAGER)
                .build();

        assertEquals(id, user.getId());
    }

    @Test
    @DisplayName("Deve permitir usuário CUSTOMER vinculado a um cliente")
    void shouldCreateCustomerUserWithCustomerRelation() {
        CustomerId customerId = CustomerId.generate();

        User user = User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.CUSTOMER)
                .customerId(customerId)
                .build();

        assertEquals(customerId, user.getCustomerId());
    }

    @Test
    @DisplayName("Não deve permitir usuário CUSTOMER sem vínculo com um cliente")
    void shouldNotCreateCustomerUserWithoutCustomerRelation() {
        assertThrows(UserCustomerRelationRequiredException.class, () -> User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.CUSTOMER)
                .build());
    }

    @Test
    @DisplayName("Deve reconstruir um usuário com todos os dados persistidos, sem revalidar o vínculo com cliente")
    void shouldRecreateUserWithAllPersistedData() {
        UserId id = UserId.generate();
        Set<UserStatusHistory> statusHistory = Set.of(UserStatusHistory.of(UserStatus.INACTIVE));

        User user = User.recreate()
                .id(id)
                .name("Maria")
                .email("maria@email.com")
                .password(Password.fromHash("hashed-password"))
                .role(UserRole.CUSTOMER)
                .lastStatus(UserStatus.INACTIVE)
                .statusHistory(statusHistory)
                .customerId(null)
                .build();

        assertEquals(id, user.getId());
        assertEquals("Maria", user.getName());
        assertEquals("maria@email.com", user.getEmail());
        assertEquals("hashed-password", user.getPassword().getValue());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(UserStatus.INACTIVE, user.getLastStatus());
        assertEquals(statusHistory, user.getStatusHistory());
        assertEquals(null, user.getCustomerId());
    }

    @Test
    @DisplayName("Deve iniciar a sessão registrando o login e o token de atualização")
    void shouldStartSession() {
        User user = createUser();
        Instant expiresAt = Instant.now().plusSeconds(3600);

        user.startSession("refresh-token-hash", expiresAt);

        assertNotNull(user.getLastLoginAt());
        assertEquals("refresh-token-hash", user.getRefreshToken());
        assertEquals(expiresAt, user.getRefreshTokenExpiresAt());
        assertTrue(user.hasValidRefreshToken(Instant.now()));
    }

    @Test
    @DisplayName("Deve rotacionar o token de atualização sem alterar o último login")
    void shouldRotateRefreshToken() {
        User user = createUser();
        user.startSession("old-hash", Instant.now().plusSeconds(60));
        Instant lastLoginAt = user.getLastLoginAt();
        Instant expiresAt = Instant.now().plusSeconds(3600);

        user.rotateRefreshToken("new-hash", expiresAt);

        assertEquals("new-hash", user.getRefreshToken());
        assertEquals(expiresAt, user.getRefreshTokenExpiresAt());
        assertEquals(lastLoginAt, user.getLastLoginAt());
    }

    @Test
    @DisplayName("Deve encerrar a sessão revogando o token de atualização")
    void shouldEndSession() {
        User user = createUser();
        user.startSession("refresh-token-hash", Instant.now().plusSeconds(3600));

        user.endSession();

        assertNull(user.getRefreshToken());
        assertNull(user.getRefreshTokenExpiresAt());
        assertFalse(user.hasValidRefreshToken(Instant.now()));
    }

    @Test
    @DisplayName("Não deve considerar válido um token de atualização expirado ou ausente")
    void shouldNotConsiderExpiredOrMissingRefreshTokenValid() {
        User user = createUser();

        assertFalse(user.hasValidRefreshToken(Instant.now()));

        user.startSession("refresh-token-hash", Instant.now().minusSeconds(1));

        assertFalse(user.hasValidRefreshToken(Instant.now()));
    }
}
