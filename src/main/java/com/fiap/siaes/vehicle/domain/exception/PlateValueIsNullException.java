package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class PlateValueIsNullException extends DomainException {

    public PlateValueIsNullException() {
        super(ErrorType.INVALID);
    }
}
