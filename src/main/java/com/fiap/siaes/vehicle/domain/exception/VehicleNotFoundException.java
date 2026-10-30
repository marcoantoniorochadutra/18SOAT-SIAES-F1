package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class VehicleNotFoundException extends DomainException {

    public VehicleNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
