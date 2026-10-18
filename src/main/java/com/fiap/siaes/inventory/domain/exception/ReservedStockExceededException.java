package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class ReservedStockExceededException extends DomainException {

    public ReservedStockExceededException(String suppliesId, int reserved, int requested) {
        super(HttpStatus.CONFLICT, suppliesId, reserved, requested);
    }
}
