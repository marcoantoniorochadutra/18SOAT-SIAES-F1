package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserEmailAlreadyExistsException extends DomainException {

    public UserEmailAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, email);
    }
}
