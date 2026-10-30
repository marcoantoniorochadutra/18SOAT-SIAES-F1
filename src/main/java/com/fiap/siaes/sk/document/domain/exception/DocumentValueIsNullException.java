package com.fiap.siaes.sk.document.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class DocumentValueIsNullException extends DomainException {

    public DocumentValueIsNullException() {
        super(ErrorType.INVALID);
    }
}
