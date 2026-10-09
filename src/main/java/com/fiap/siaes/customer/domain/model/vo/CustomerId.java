package com.fiap.siaes.customer.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record CustomerId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public CustomerId(String raw) {
        this(UUID.fromString(raw));
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(UUID id) {
        return new CustomerId(id);
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class CustomerIdJavaType extends UUIDWrapperJavaType<CustomerId> {
        protected CustomerIdJavaType() {
            super(CustomerId.class, CustomerId::new);
        }
    }

}
