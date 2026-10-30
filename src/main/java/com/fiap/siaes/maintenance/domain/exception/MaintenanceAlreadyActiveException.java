package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceAlreadyActiveException extends DomainException {

    public MaintenanceAlreadyActiveException(String id) {
        super(ErrorType.CONFLICT, id);
    }
}
