package com.fiap.siaes.user.infrastructure.persistence.mapper;

import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.UserStatusHistory;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpaEntity;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserStatusHistoryJpaEntity;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@UtilityClass
public class UserMapper {

    public static UserJpaEntity toEntity(User user) {
        if (isNull(user))
            return null;

        return UserJpaEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword().getValue())
                .role(user.getRole())
                .lastStatus(user.getLastStatus())
                .statusHistory(toHistoryEntities(user.getStatusHistory()))
                .customerId(user.getCustomerId())
                .lastLoginAt(user.getLastLoginAt())
                .refreshToken(user.getRefreshToken())
                .build();
    }

    public static User toDomain(UserJpaEntity entity) {
        if (isNull(entity))
            return null;

        return User.recreate()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(Password.recreate(entity.getPassword()))
                .role(entity.getRole())
                .lastStatus(entity.getLastStatus())
                .statusHistory(toHistoryDomain(entity.getStatusHistory()))
                .customerId(entity.getCustomerId())
                .lastLoginAt(entity.getLastLoginAt())
                .refreshToken(entity.getRefreshToken())
                .build();
    }

    private static Set<UserStatusHistoryJpaEntity> toHistoryEntities(Set<UserStatusHistory> statusHistory) {
        if (isNull(statusHistory))
            return new HashSet<>();

        return statusHistory.stream()
                .map(history -> new UserStatusHistoryJpaEntity(history.getId(), history.getStatus(), history.getUpdatedAt(), null))
                .collect(Collectors.toSet());
    }

    private static Set<UserStatusHistory> toHistoryDomain(Set<UserStatusHistoryJpaEntity> statusHistory) {
        if (isNull(statusHistory))
            return new HashSet<>();

        return statusHistory.stream()
                .map(history -> new UserStatusHistory(history.getId(), history.getStatus(), history.getUpdatedAt(), null))
                .collect(Collectors.toSet());
    }
}
