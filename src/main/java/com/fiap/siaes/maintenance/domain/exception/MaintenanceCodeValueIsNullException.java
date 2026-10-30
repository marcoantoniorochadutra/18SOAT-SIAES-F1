package com.fiap.siaes.maintenance.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class MaintenanceCodeValueIsNullException extends DomainException {

    public MaintenanceCodeValueIsNullException() {
        super(ErrorType.INVALID);
    }
}
