package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;

public interface UpdateDiagnosisUseCase {

    Diagnosis execute(DiagnosisId id, UpdateDiagnosisCommand command);

    record UpdateDiagnosisCommand(String brand, String model, int year) {}
}
