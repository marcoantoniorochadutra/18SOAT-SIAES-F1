package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class UserEmailOrCustomerRequiredException extends DomainException {

    public UserEmailOrCustomerRequiredException() {
        super(ErrorType.INVALID);
    }
}
