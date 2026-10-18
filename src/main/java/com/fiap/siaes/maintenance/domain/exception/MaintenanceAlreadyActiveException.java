package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceAlreadyActiveException extends DomainException {

    public MaintenanceAlreadyActiveException(String id) {
        super(HttpStatus.CONFLICT, id);
    }
}
