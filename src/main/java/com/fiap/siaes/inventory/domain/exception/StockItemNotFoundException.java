package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class StockItemNotFoundException extends DomainException {

    public StockItemNotFoundException(String suppliesId) {
        super(ErrorType.NOT_FOUND, suppliesId);
    }
}
