package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationItemsRequiredException extends DomainException {

    public QuotationItemsRequiredException() {
        super(ErrorType.INVALID);
    }
}
