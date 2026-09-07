package com.fiap.siaes.quotation.application.usecase.diagnosis.application.service;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.application.usecase.UpdateDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDiagnosisAppService implements UpdateDiagnosisUseCase {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    @Transactional
    public Diagnosis execute(DiagnosisId id, UpdateDiagnosisCommand command) {
        Diagnosis diagnosis = this.diagnosisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        diagnosis.setBrand(command.brand());
        diagnosis.setModel(command.model());
        diagnosis.setYear(command.year());

        return this.diagnosisRepository.save(diagnosis);
    }
}
