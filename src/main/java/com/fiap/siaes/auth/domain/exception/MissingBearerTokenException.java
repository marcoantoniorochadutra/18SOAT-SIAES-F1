package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MissingBearerTokenException extends DomainException {

    public MissingBearerTokenException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
