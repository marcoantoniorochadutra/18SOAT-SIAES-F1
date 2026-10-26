package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class InactiveMaintenanceException extends DomainException {

    public InactiveMaintenanceException(String maintenanceId) {
        super(HttpStatus.CONFLICT, maintenanceId);
    }
}
