package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SuppliesAlreadyActiveException extends DomainException {

    public SuppliesAlreadyActiveException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
