package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class SuppliesAlreadyInactiveException extends DomainException {

    public SuppliesAlreadyInactiveException(String id) {
        super(HttpStatus.CONFLICT, id);
    }
}
