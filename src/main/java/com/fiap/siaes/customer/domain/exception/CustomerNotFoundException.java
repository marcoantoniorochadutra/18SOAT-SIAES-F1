package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(String id) {
        super(ErrorType.NOT_FOUND, id);
    }
}
