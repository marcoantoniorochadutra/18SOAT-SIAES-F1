package com.fiap.siaes.maintenance.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infrastructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record MaintenanceId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public MaintenanceId(String raw) {
        this(UUID.fromString(raw));
    }

    public static MaintenanceId generate() {
        return new MaintenanceId(UUID.randomUUID());
    }

    public static MaintenanceId from(UUID id) {
        return new MaintenanceId(id);
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class MaintenanceIdJavaType extends UUIDWrapperJavaType<MaintenanceId> {
        protected MaintenanceIdJavaType() {
            super(MaintenanceId.class, MaintenanceId::new);
        }
    }

}
