package com.fiap.siaes.vehicle.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class VehiclePlateAlreadyExistsException extends DomainException {

    public VehiclePlateAlreadyExistsException(String plate) {
        super(HttpStatus.CONFLICT, plate);
    }
}
