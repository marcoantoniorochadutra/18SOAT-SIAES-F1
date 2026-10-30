package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenancePriceInvalidException extends DomainException {

    public MaintenancePriceInvalidException() {
        super(ErrorType.INVALID);
    }
}
