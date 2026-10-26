package com.fiap.siaes.quotation.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record QuotationApprovalTokenId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public QuotationApprovalTokenId(String raw) {
        this(UUID.fromString(raw));
    }

    public static QuotationApprovalTokenId generate() {
        return new QuotationApprovalTokenId(UUID.randomUUID());
    }

    public static QuotationApprovalTokenId from(UUID id) {
        return new QuotationApprovalTokenId(id);
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class QuotationApprovalTokenIdJavaType extends UUIDWrapperJavaType<QuotationApprovalTokenId> {
        protected QuotationApprovalTokenIdJavaType() {
            super(QuotationApprovalTokenId.class, QuotationApprovalTokenId::new);
        }
    }

}
