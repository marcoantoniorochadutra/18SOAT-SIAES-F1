package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VehicleBrandRequiredException extends DomainException {

    public VehicleBrandRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
