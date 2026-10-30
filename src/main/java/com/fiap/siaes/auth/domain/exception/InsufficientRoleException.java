package com.fiap.siaes.auth.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;
import com.fiap.siaes.user.domain.model.enums.UserRole;

public class InsufficientRoleException extends DomainException {

    public InsufficientRoleException(UserRole minimumRole) {
        super(ErrorType.FORBIDDEN, minimumRole);
    }
}
