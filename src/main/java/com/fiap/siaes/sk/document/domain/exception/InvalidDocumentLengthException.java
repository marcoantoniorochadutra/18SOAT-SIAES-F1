package com.fiap.siaes.sk.document.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InvalidDocumentLengthException extends DomainException {

    public InvalidDocumentLengthException(String document) {
        super(ErrorType.INVALID, document);
    }
}
