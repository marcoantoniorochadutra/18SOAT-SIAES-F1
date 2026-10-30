package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SuppliesDescriptionRequiredException extends DomainException {

    public SuppliesDescriptionRequiredException() {
        super(ErrorType.INVALID);
    }
}
