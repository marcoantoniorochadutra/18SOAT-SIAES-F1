package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationOdometerInvalidException extends DomainException {

    public QuotationOdometerInvalidException() {
        super(ErrorType.INVALID);
    }
}
