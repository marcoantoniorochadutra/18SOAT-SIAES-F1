package com.fiap.siaes.quotation.domain.repository;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository {

    Diagnosis save(Diagnosis diagnosis);

    Optional<Diagnosis> findById(DiagnosisId id);

    List<Diagnosis> findAll();

    List<Diagnosis> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);

    void deleteById(DiagnosisId id);
}
