package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderDiagnosisDescriptionRequiredException extends DomainException {

    public WorkOrderDiagnosisDescriptionRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
