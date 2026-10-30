package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InvalidVehicleYearException extends DomainException {

    public InvalidVehicleYearException(int year) {
        super(ErrorType.INVALID, year);
    }
}
