package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;

public interface DeleteDiagnosisUseCase {

    void execute(DiagnosisId id);
}
