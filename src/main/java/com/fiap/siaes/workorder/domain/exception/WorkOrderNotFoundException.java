package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderNotFoundException extends DomainException {

    public WorkOrderNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
