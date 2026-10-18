package com.fiap.siaes.inventory.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record StockMovementId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public StockMovementId(String raw) {
        this(UUID.fromString(raw));
    }

    public static StockMovementId generate() {
        return new StockMovementId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class StockMovementIdJavaType extends UUIDWrapperJavaType<StockMovementId> {
        protected StockMovementIdJavaType() {
            super(StockMovementId.class, StockMovementId::new);
        }
    }

}
