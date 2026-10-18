package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidStockQuantityException extends DomainException {

    public InvalidStockQuantityException(String suppliesId, int quantity) {
        super(HttpStatus.BAD_REQUEST, suppliesId, quantity);
    }
}
