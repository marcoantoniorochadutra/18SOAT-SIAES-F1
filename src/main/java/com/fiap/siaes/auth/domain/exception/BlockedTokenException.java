package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class BlockedTokenException extends DomainException {

    public BlockedTokenException() {
        super(HttpStatus.UNAUTHORIZED);
    }
}
