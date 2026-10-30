package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class UndeclaredEndpointAccessException extends DomainException {

    public UndeclaredEndpointAccessException() {
        super(ErrorType.UNAUTHENTICATED);
    }
}
