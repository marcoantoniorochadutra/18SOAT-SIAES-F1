package com.fiap.siaes.quotation.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record QuotationId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public QuotationId(String raw) {
        this(UUID.fromString(raw));
    }

    public static QuotationId generate() {
        return new QuotationId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class QuotationIdJavaType extends UUIDWrapperJavaType<QuotationId> {
        protected QuotationIdJavaType() {
            super(QuotationId.class, QuotationId::new);
        }
    }

}
