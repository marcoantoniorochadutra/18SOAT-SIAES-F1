package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderDiagnosisNotAllowedException extends DomainException {

    public WorkOrderDiagnosisNotAllowedException(String id, String currentStatus) {
        super(HttpStatus.CONFLICT, id, currentStatus);
    }
}
