package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class CustomerEmailAlreadyExistsException extends DomainException {

    public CustomerEmailAlreadyExistsException(String email) {
        super(ErrorType.CONFLICT, email);
    }
}
