package com.fiap.siaes.quotation.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record QuotationSuppliesId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public QuotationSuppliesId(String raw) {
        this(UUID.fromString(raw));
    }

    public static QuotationSuppliesId generate() {
        return new QuotationSuppliesId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class QuotationSuppliesIdJavaType extends UUIDWrapperJavaType<QuotationSuppliesId> {
        protected QuotationSuppliesIdJavaType() {
            super(QuotationSuppliesId.class, QuotationSuppliesId::new);
        }
    }

}
