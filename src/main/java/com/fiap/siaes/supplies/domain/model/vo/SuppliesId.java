package com.fiap.siaes.supplies.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record SuppliesId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public SuppliesId(String raw) {
        this(UUID.fromString(raw));
    }

    public static SuppliesId generate() {
        return new SuppliesId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class SuppliesIdJavaType extends UUIDWrapperJavaType<SuppliesId> {
        protected SuppliesIdJavaType() {
            super(SuppliesId.class, SuppliesId::new);
        }
    }

}
