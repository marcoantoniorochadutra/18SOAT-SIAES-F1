package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationItemQuantityInvalidException extends DomainException {

    public QuotationItemQuantityInvalidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
