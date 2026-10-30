package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class UserCustomerRelationRequiredException extends DomainException {

    public UserCustomerRelationRequiredException() {
        super(ErrorType.INVALID);
    }
}
