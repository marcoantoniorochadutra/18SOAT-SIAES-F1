package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;

import java.util.List;

public interface ListDiagnosisUseCase {

    List<Diagnosis> execute();
}
