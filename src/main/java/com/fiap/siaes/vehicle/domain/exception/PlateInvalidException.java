package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PlateInvalidException extends DomainException {

    public PlateInvalidException(String plate) {
        super(HttpStatus.BAD_REQUEST, plate);
    }
}
