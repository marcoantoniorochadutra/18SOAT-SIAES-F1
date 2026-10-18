package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class StockItemNotFoundException extends DomainException {

    public StockItemNotFoundException(String suppliesId) {
        super(HttpStatus.NOT_FOUND, suppliesId);
    }
}
