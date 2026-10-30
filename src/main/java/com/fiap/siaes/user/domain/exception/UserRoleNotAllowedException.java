package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class UserRoleNotAllowedException extends DomainException {

    public UserRoleNotAllowedException(String role) {
        super(ErrorType.FORBIDDEN, role);
    }
}
