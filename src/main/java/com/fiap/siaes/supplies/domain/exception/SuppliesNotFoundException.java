package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class SuppliesNotFoundException extends DomainException {

    public SuppliesNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
