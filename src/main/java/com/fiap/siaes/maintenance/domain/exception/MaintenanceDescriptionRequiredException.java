package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class MaintenanceDescriptionRequiredException extends DomainException {

    public MaintenanceDescriptionRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
