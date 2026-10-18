package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidStockMovementTypeException extends DomainException {

    public InvalidStockMovementTypeException(String suppliesId, String type) {
        super(HttpStatus.BAD_REQUEST, suppliesId, type);
    }
}
