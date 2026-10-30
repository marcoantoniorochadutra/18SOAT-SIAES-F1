package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationAlreadyOpenException extends DomainException {

    public QuotationAlreadyOpenException(String workOrderId) {
        super(ErrorType.CONFLICT, workOrderId);
    }
}
