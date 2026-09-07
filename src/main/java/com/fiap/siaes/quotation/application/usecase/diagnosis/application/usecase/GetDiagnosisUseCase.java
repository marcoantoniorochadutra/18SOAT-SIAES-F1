package com.fiap.siaes.quotation.application.usecase.diagnosis.application.usecase;

import com.fiap.siaes.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;

public interface GetDiagnosisUseCase {

    Diagnosis execute(DiagnosisId id);
}
