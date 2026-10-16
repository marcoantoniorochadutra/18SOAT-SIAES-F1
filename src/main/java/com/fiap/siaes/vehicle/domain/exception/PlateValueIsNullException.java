package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class PlateValueIsNullException extends DomainException {

    public PlateValueIsNullException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
