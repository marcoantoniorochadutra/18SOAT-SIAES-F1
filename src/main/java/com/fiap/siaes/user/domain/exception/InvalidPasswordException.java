package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super(ErrorType.INVALID);
    }
}
