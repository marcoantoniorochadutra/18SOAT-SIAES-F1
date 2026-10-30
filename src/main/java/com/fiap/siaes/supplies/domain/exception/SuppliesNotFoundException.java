package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SuppliesNotFoundException extends DomainException {

    public SuppliesNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
