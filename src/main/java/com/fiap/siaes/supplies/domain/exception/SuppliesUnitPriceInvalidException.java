package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class SuppliesUnitPriceInvalidException extends DomainException {

    public SuppliesUnitPriceInvalidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
