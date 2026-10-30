package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceNotFoundException extends DomainException {

    public MaintenanceNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
