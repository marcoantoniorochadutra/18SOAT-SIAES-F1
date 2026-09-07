package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase;

import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;

public interface CreateDiagnosisUseCase {

    DiagnosisId execute(CreateDiagnosisCommand command);

    record CreateDiagnosisCommand(String ownerId, String plate, String brand, String model, int year) {}
}
