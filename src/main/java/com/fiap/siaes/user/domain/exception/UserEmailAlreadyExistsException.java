package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class UserEmailAlreadyExistsException extends DomainException {

    public UserEmailAlreadyExistsException(String email) {
        super(ErrorType.CONFLICT, email);
    }
}
