package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class SuppliesSkuAlreadyExistsException extends DomainException {

    public SuppliesSkuAlreadyExistsException(String sku) {
        super(HttpStatus.CONFLICT, sku);
    }
}
