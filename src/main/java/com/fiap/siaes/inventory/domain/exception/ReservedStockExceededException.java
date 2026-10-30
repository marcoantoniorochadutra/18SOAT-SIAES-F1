package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class ReservedStockExceededException extends DomainException {

    public ReservedStockExceededException(String suppliesId, int reserved, int requested) {
        super(ErrorType.CONFLICT, suppliesId, reserved, requested);
    }
}
