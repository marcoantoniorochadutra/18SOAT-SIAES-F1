package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserCustomerRelationRequiredException extends DomainException {

    public UserCustomerRelationRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
