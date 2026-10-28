package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderDiagnosisRequiredException extends DomainException {

    public WorkOrderDiagnosisRequiredException(String id) {
        super(HttpStatus.CONFLICT, id);
    }
}
