package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PasswordValueIsNullException extends DomainException {

    public PasswordValueIsNullException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
