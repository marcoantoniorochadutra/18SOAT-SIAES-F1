package com.fiap.siaes.quotation.application.usecase.diagnosis.application.service;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.application.usecase.DeleteDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDiagnosisAppService implements DeleteDiagnosisUseCase {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    @Transactional
    public void execute(DiagnosisId id) {
        this.diagnosisRepository.deleteById(id);
    }
}
