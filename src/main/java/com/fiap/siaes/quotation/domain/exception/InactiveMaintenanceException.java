package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class InactiveMaintenanceException extends DomainException {

    public InactiveMaintenanceException(String maintenanceId) {
        super(ErrorType.CONFLICT, maintenanceId);
    }
}
