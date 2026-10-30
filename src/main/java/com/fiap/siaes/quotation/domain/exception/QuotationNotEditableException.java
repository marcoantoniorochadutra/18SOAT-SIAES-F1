package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationNotEditableException extends DomainException {

    public QuotationNotEditableException(String id, String status) {
        super(ErrorType.CONFLICT, id, status);
    }
}
