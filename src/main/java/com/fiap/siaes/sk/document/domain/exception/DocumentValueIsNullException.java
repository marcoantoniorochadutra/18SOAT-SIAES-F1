package com.fiap.siaes.sk.document.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class DocumentValueIsNullException extends DomainException {

    public DocumentValueIsNullException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
