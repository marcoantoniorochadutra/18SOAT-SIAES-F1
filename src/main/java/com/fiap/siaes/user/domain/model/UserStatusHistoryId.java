package com.fiap.siaes.user.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record UserStatusHistoryId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public UserStatusHistoryId(String raw) {
        this(UUID.fromString(raw));
    }

    public static UserStatusHistoryId generate() {
        return new UserStatusHistoryId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class UserStatusHistoryIdJavaType extends UUIDWrapperJavaType<UserStatusHistoryId> {
        protected UserStatusHistoryIdJavaType() {
            super(UserStatusHistoryId.class, UserStatusHistoryId::new);
        }
    }

}
