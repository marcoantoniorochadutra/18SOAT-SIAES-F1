package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VehicleNotFoundException extends DomainException {

    public VehicleNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
