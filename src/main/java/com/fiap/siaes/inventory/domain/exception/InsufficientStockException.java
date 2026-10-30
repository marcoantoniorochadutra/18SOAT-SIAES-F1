package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String suppliesId, int available, int requested) {
        super(ErrorType.CONFLICT, suppliesId, available, requested);
    }
}
