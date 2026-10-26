package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationNotEditableException extends DomainException {

    public QuotationNotEditableException(String id, String status) {
        super(HttpStatus.CONFLICT, id, status);
    }
}
