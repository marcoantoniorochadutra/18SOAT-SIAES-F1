package com.fiap.siaes.quotation.application.usecase.diagnosis.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record DiagnosisId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public DiagnosisId(String raw) {
        this(UUID.fromString(raw));
    }

    public static DiagnosisId generate() {
        return new DiagnosisId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class DiagnosisIdJavaType extends UUIDWrapperJavaType<DiagnosisId> {
        protected DiagnosisIdJavaType() {
            super(DiagnosisId.class, DiagnosisId::new);
        }
    }

}
