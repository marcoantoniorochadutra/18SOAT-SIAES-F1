package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class PlateInvalidException extends DomainException {

    public PlateInvalidException(String plate) {
        super(ErrorType.INVALID, plate);
    }
}
