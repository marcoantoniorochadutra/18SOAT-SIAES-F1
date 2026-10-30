package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SuppliesSkuAlreadyExistsException extends DomainException {

    public SuppliesSkuAlreadyExistsException(String sku) {
        super(ErrorType.CONFLICT, sku);
    }
}
