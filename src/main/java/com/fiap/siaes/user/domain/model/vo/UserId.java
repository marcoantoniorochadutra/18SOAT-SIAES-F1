package com.fiap.siaes.user.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record UserId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public UserId(String raw) {
        this(UUID.fromString(raw));
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId from(UUID id) {
        return new UserId(id);
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class UserIdJavaType extends UUIDWrapperJavaType<UserId> {
        protected UserIdJavaType() {
            super(UserId.class, UserId::new);
        }
    }

}
