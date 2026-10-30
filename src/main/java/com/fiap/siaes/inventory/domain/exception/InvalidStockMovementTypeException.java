package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InvalidStockMovementTypeException extends DomainException {

    public InvalidStockMovementTypeException(String suppliesId, String type) {
        super(ErrorType.INVALID, suppliesId, type);
    }
}
