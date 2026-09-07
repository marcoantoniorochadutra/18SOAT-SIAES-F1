package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase;

import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;

public interface DeleteDiagnosisUseCase {

    void execute(DiagnosisId id);
}
