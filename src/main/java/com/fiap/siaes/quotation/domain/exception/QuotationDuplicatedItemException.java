package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationDuplicatedItemException extends DomainException {

    public QuotationDuplicatedItemException(String itemId) {
        super(HttpStatus.BAD_REQUEST, itemId);
    }
}
