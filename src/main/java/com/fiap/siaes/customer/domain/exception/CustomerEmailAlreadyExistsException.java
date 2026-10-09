package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CustomerEmailAlreadyExistsException extends DomainException {

    public CustomerEmailAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, email);
    }
}
