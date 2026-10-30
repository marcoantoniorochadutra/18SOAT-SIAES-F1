package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InactiveSuppliesException extends DomainException {

    public InactiveSuppliesException(String suppliesId) {
        super(ErrorType.CONFLICT, suppliesId);
    }
}
