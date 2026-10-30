package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class VehicleModelRequiredException extends DomainException {

    public VehicleModelRequiredException() {
        super(ErrorType.INVALID);
    }
}
