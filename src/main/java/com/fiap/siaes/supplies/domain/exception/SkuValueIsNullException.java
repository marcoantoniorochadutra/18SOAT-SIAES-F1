package com.fiap.siaes.supplies.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class SkuValueIsNullException extends DomainException {

    public SkuValueIsNullException() {
        super(ErrorType.INVALID);
    }
}
