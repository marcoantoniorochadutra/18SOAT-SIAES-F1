package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationDuplicatedItemException extends DomainException {

    public QuotationDuplicatedItemException(String itemId) {
        super(ErrorType.INVALID, itemId);
    }
}
