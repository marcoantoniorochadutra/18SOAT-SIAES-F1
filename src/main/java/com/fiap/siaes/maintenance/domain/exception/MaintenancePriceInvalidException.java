package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenancePriceInvalidException extends DomainException {

    public MaintenancePriceInvalidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
