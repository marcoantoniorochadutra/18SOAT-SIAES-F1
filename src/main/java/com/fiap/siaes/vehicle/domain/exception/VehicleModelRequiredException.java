package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VehicleModelRequiredException extends DomainException {

    public VehicleModelRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
