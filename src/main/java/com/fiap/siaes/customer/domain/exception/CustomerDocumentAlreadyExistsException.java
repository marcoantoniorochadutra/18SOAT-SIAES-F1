package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CustomerDocumentAlreadyExistsException extends DomainException {

    public CustomerDocumentAlreadyExistsException(String document) {
        super(HttpStatus.CONFLICT, document);
    }
}
