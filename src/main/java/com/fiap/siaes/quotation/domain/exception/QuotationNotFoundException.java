package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationNotFoundException extends DomainException {

    public QuotationNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
