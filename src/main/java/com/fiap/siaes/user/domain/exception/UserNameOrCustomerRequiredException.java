package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserNameOrCustomerRequiredException extends DomainException {

    public UserNameOrCustomerRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
