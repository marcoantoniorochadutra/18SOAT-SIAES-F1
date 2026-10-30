package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderOpenQuotationException extends DomainException {

    public WorkOrderOpenQuotationException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
