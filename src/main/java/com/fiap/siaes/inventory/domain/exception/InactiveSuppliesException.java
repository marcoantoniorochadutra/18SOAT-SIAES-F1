package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InactiveSuppliesException extends DomainException {

    public InactiveSuppliesException(String suppliesId) {
        super(HttpStatus.CONFLICT, suppliesId);
    }
}
