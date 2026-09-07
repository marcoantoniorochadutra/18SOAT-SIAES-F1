package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase.diagnosis.application.service;

import com.fiap.siaes.diagnosis.application.usecase.GetDiagnosisUseCase;
import com.fiap.siaes.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.domain.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDiagnosisAppService implements GetDiagnosisUseCase {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    public Diagnosis execute(DiagnosisId id) {
        return this.diagnosisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }
}
