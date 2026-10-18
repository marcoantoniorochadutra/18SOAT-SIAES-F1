package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceEstimatedDurationInvalidException extends DomainException {

    public MaintenanceEstimatedDurationInvalidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
