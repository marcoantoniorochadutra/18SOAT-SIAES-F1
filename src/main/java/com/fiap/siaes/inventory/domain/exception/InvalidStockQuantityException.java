package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InvalidStockQuantityException extends DomainException {

    public InvalidStockQuantityException(String suppliesId, int quantity) {
        super(ErrorType.INVALID, suppliesId, quantity);
    }
}
