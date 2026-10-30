package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class CustomerNameRequiredException extends DomainException {

    public CustomerNameRequiredException() {
        super(ErrorType.INVALID);
    }
}
