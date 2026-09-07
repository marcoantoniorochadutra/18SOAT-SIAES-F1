package com.fiap.siaes.quotation.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.infrastructure.persistence.entity.DiagnosisJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisJpaRepository extends JpaRepository<DiagnosisJpaEntity, DiagnosisId> {

    List<DiagnosisJpaEntity> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);
}
