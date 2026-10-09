package com.fiap.siaes.user.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class UserRoleNotAllowedException extends DomainException {

    public UserRoleNotAllowedException(String role) {
        super(HttpStatus.FORBIDDEN, role);
    }
}
