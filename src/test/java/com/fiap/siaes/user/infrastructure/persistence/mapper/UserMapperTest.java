package com.fiap.siaes.user.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.UserStatusHistory;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.model.vo.UserStatusHistoryId;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpaEntity;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserStatusHistoryJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Mapper - User")
class UserMapperTest {

    @Test
    @DisplayName("Deve mapear User para UserJpaEntity")
    void shouldMapUserToEntity() {
        var statusHistory = UserStatusHistory.of(UserStatus.ACTIVE);
        var user = User.recreate()
                .id(UserId.generate())
                .name("Maria Silva")
                .email("maria@email.com")
                .password(Password.recreate("hashed-password"))
                .role(UserRole.MANAGER)
                .lastStatus(UserStatus.ACTIVE)
                .statusHistory(Set.of(statusHistory))
                .customerId(CustomerId.generate())
                .lastLoginAt(Instant.now())
                .refreshToken("refresh-token")
                .build();

        UserJpaEntity actualEntity = UserMapper.toEntity(user);

        assertEquals(user.getId(), actualEntity.getId());
        assertEquals(user.getName(), actualEntity.getName());
        assertEquals(user.getEmail(), actualEntity.getEmail());
        assertEquals(user.getPassword().getValue(), actualEntity.getPassword());
        assertEquals(user.getRole(), actualEntity.getRole());
        assertEquals(user.getLastStatus(), actualEntity.getLastStatus());
        assertEquals(user.getCustomerId(), actualEntity.getCustomerId());
        assertEquals(user.getLastLoginAt(), actualEntity.getLastLoginAt());
        assertEquals(user.getRefreshToken(), actualEntity.getRefreshToken());
        assertEquals(1, actualEntity.getStatusHistory().size());
        UserStatusHistoryJpaEntity historyEntity = actualEntity.getStatusHistory().iterator().next();
        assertEquals(statusHistory.getId(), historyEntity.getId());
        assertEquals(statusHistory.getStatus(), historyEntity.getStatus());
        assertEquals(statusHistory.getUpdatedAt(), historyEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve mapear UserJpaEntity para User")
    void shouldMapEntityToDomain() {
        var historyEntity = new UserStatusHistoryJpaEntity(
                UserStatusHistoryId.generate(), UserStatus.ACTIVE, Instant.now(), null);
        var entity = new UserJpaEntity(
                UserId.generate(),
                "Maria Silva",
                "maria@email.com",
                "hashed-password",
                UserRole.MANAGER,
                UserStatus.ACTIVE,
                Set.of(historyEntity),
                CustomerId.generate(),
                Instant.now(),
                "refresh-token");

        User actualUser = UserMapper.toDomain(entity);

        assertEquals(entity.getId(), actualUser.getId());
        assertEquals(entity.getName(), actualUser.getName());
        assertEquals(entity.getEmail(), actualUser.getEmail());
        assertEquals(entity.getPassword(), actualUser.getPassword().getValue());
        assertEquals(entity.getRole(), actualUser.getRole());
        assertEquals(entity.getLastStatus(), actualUser.getLastStatus());
        assertEquals(entity.getCustomerId(), actualUser.getCustomerId());
        assertEquals(entity.getLastLoginAt(), actualUser.getLastLoginAt());
        assertEquals(entity.getRefreshToken(), actualUser.getRefreshToken());
        assertEquals(1, actualUser.getStatusHistory().size());
        UserStatusHistory history = actualUser.getStatusHistory().iterator().next();
        assertEquals(historyEntity.getId(), history.getId());
        assertEquals(historyEntity.getStatus(), history.getStatus());
        assertEquals(historyEntity.getUpdatedAt(), history.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve mapear para conjunto de histórico vazio quando User não possui histórico")
    void shouldMapToEmptyHistoryWhenUserHistoryIsNull() {
        var user = User.recreate()
                .id(UserId.generate())
                .name("Maria Silva")
                .email("maria@email.com")
                .password(Password.recreate("hashed-password"))
                .role(UserRole.MANAGER)
                .build();

        UserJpaEntity actualEntity = UserMapper.toEntity(user);

        assertTrue(actualEntity.getStatusHistory().isEmpty());
    }

    @Test
    @DisplayName("Deve mapear para conjunto de histórico vazio quando UserJpaEntity não possui histórico")
    void shouldMapToEmptyHistoryWhenEntityHistoryIsNull() {
        var entity = new UserJpaEntity(
                UserId.generate(), "Maria Silva", "maria@email.com", "hashed-password",
                UserRole.MANAGER, UserStatus.ACTIVE, null, null, null, null);

        User actualUser = UserMapper.toDomain(entity);

        assertTrue(actualUser.getStatusHistory().isEmpty());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando User não informado")
    void shouldMapToNullWhenUserIsNull() {
        assertNull(UserMapper.toEntity(null));
    }

    @Test
    @DisplayName("Deve mapear para nulo quando UserJpaEntity não informado")
    void shouldMapToNullWhenEntityIsNull() {
        assertNull(UserMapper.toDomain(null));
    }
}
