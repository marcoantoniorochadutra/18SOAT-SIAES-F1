package com.fiap.siaes.vehicle.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record VehicleId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public VehicleId(String raw) {
        this(UUID.fromString(raw));
    }

    public static VehicleId generate() {
        return new VehicleId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class VehicleIdJavaType extends UUIDWrapperJavaType<VehicleId> {
        protected VehicleIdJavaType() {
            super(VehicleId.class, VehicleId::new);
        }
    }

}
