package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceAlreadyInactiveException extends DomainException {

    public MaintenanceAlreadyInactiveException(String id) {
        super(HttpStatus.CONFLICT, id);
    }
}
