package com.fiap.siaes.sk.document.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidDocumentException extends DomainException {

    public InvalidDocumentException(String document) {
        super(HttpStatus.BAD_REQUEST, document);
    }
}
