package com.fiap.siaes.quotation.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record QuotationMaintenanceId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public QuotationMaintenanceId(String raw) {
        this(UUID.fromString(raw));
    }

    public static QuotationMaintenanceId generate() {
        return new QuotationMaintenanceId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class QuotationMaintenanceIdJavaType extends UUIDWrapperJavaType<QuotationMaintenanceId> {
        protected QuotationMaintenanceIdJavaType() {
            super(QuotationMaintenanceId.class, QuotationMaintenanceId::new);
        }
    }
}
