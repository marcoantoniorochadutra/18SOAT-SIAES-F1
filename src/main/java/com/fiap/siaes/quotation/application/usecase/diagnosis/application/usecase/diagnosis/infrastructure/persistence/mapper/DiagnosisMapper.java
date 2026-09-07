package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase.diagnosis.infrastructure.persistence.mapper;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.infrastructure.persistence.entity.DiagnosisJpaEntity;

public final class DiagnosisMapper {

    private DiagnosisMapper() {
    }

    public static DiagnosisJpaEntity toEntity(Diagnosis diagnosis) {
        if (diagnosis == null) {
            return null;
        }
        return new DiagnosisJpaEntity(
                diagnosis.getId(),
                diagnosis.getOwnerId(),
                diagnosis.getPlate(),
                diagnosis.getBrand(),
                diagnosis.getModel(),
                diagnosis.getYear()
        );
    }

    public static Diagnosis toDomain(DiagnosisJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Diagnosis.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .plate(entity.getPlate())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .build();
    }
}
