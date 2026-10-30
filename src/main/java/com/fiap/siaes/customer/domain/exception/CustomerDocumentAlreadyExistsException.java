package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class CustomerDocumentAlreadyExistsException extends DomainException {

    public CustomerDocumentAlreadyExistsException(String document) {
        super(ErrorType.CONFLICT, document);
    }
}
