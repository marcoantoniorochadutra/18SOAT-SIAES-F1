package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.diagnosis.domain.model.Diagnosis;

import java.util.List;

public interface ListDiagnosisUseCase {

    List<Diagnosis> execute();
}
