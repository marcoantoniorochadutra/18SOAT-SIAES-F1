package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class BlockedTokenException extends DomainException {

    public BlockedTokenException() {
        super(ErrorType.UNAUTHENTICATED);
    }
}
