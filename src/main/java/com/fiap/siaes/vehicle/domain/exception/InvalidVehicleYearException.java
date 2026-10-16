package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidVehicleYearException extends DomainException {

    public InvalidVehicleYearException(int year) {
        super(HttpStatus.BAD_REQUEST, year);
    }
}
