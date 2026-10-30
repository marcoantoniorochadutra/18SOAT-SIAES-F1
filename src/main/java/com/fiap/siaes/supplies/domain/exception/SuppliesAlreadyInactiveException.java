package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SuppliesAlreadyInactiveException extends DomainException {

    public SuppliesAlreadyInactiveException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
