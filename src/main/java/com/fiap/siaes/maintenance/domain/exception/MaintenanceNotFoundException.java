package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceNotFoundException extends DomainException {

    public MaintenanceNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
