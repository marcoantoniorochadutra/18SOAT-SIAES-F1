package com.fiap.siaes.user.infrastructure.persistence.mapper;

import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpa;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserJpa toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserJpa(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCustomerId()
        );
    }

    public static User toDomain(UserJpa entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .customerId(entity.getCustomerId())
                .build();
    }
}
