package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;
import com.fiap.siaes.user.domain.model.enums.UserRole;

public class RoleNotAllowedException extends DomainException {

    public RoleNotAllowedException(UserRole role) {
        super(ErrorType.FORBIDDEN, role);
    }
}
