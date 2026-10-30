package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class VehiclePlateAlreadyExistsException extends DomainException {

    public VehiclePlateAlreadyExistsException(String plate) {
        super(ErrorType.CONFLICT, plate);
    }
}
