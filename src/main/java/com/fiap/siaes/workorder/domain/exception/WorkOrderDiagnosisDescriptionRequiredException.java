package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderDiagnosisDescriptionRequiredException extends DomainException {

    public WorkOrderDiagnosisDescriptionRequiredException() {
        super(ErrorType.INVALID);
    }
}
