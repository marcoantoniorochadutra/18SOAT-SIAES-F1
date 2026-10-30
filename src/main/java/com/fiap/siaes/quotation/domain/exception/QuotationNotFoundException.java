package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationNotFoundException extends DomainException {

    public QuotationNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
