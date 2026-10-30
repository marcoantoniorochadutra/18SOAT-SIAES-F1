package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceCodeAlreadyExistsException extends DomainException {

    public MaintenanceCodeAlreadyExistsException(String code) {
        super(ErrorType.CONFLICT, code);
    }
}
