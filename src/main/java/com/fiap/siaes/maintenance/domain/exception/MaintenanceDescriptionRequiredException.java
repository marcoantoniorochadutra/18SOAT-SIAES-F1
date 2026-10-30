package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceDescriptionRequiredException extends DomainException {

    public MaintenanceDescriptionRequiredException() {
        super(ErrorType.INVALID);
    }
}
