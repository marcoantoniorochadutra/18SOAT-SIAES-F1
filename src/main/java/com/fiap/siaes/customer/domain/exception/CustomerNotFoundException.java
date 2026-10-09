package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
