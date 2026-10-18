package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String suppliesId, int available, int requested) {
        super(HttpStatus.CONFLICT, suppliesId, available, requested);
    }
}
