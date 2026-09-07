package com.fiap.siaes.quotation.application.usecase.diagnosis.application.service;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.application.usecase.ListDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListDiagnosisAppService implements ListDiagnosisUseCase {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    public List<Diagnosis> execute() {
        return this.diagnosisRepository.findAll();
    }
}
