package com.fiap.siaes.customer.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class CustomerNameRequiredException extends DomainException {

    public CustomerNameRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
