package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderDiagnosisRequiredException extends DomainException {

    public WorkOrderDiagnosisRequiredException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
