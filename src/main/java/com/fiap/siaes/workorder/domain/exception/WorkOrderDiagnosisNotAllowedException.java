package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderDiagnosisNotAllowedException extends DomainException {

    public WorkOrderDiagnosisNotAllowedException(String id, String currentStatus) {
        super(ErrorType.CONFLICT, id, currentStatus);
    }
}
