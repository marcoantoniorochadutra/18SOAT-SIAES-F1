package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class PasswordValueIsNullException extends DomainException {

    public PasswordValueIsNullException() {
        super(ErrorType.INVALID);
    }
}
