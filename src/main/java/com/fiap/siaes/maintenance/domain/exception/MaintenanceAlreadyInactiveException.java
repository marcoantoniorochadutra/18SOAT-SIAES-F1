package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceAlreadyInactiveException extends DomainException {

    public MaintenanceAlreadyInactiveException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
