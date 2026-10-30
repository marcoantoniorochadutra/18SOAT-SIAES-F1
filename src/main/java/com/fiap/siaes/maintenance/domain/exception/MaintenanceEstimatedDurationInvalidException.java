package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceEstimatedDurationInvalidException extends DomainException {

    public MaintenanceEstimatedDurationInvalidException() {
        super(ErrorType.INVALID);
    }
}
