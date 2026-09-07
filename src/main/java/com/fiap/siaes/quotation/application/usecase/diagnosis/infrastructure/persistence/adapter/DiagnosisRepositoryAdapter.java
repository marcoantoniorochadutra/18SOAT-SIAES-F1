package com.fiap.siaes.quotation.application.usecase.diagnosis.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.repository.DiagnosisRepository;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.infrastructure.persistence.mapper.DiagnosisMapper;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.infrastructure.persistence.repository.DiagnosisJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiagnosisRepositoryAdapter implements DiagnosisRepository {

    private final DiagnosisJpaRepository diagnosisJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Diagnosis save(Diagnosis diagnosis) {
        var entity = DiagnosisMapper.toEntity(diagnosis);
        var saved = this.diagnosisJpaRepository.save(entity);
        return DiagnosisMapper.toDomain(saved);
    }

    @Override
    public Optional<Diagnosis> findById(DiagnosisId id) {
        return this.diagnosisJpaRepository.findById(id).map(DiagnosisMapper::toDomain);
    }

    @Override
    public List<Diagnosis> findAll() {
        return this.diagnosisJpaRepository.findAll().stream().map(DiagnosisMapper::toDomain).toList();
    }

    @Override
    public List<Diagnosis> findByOwnerId(CustomerId ownerId) {
        return this.diagnosisJpaRepository.findByOwnerId(ownerId).stream().map(DiagnosisMapper::toDomain).toList();
    }

    @Override
    public boolean existsByPlate(String plate) {
        return this.diagnosisJpaRepository.existsByPlate(plate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(DiagnosisId id) {
        this.diagnosisJpaRepository.deleteById(id);
    }
}
