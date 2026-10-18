package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceCodeAlreadyExistsException extends DomainException {

    public MaintenanceCodeAlreadyExistsException(String code) {
        super(HttpStatus.CONFLICT, code);
    }
}
