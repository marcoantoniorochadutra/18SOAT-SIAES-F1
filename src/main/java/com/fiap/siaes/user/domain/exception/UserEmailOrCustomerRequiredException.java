package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserEmailOrCustomerRequiredException extends DomainException {

    public UserEmailOrCustomerRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
