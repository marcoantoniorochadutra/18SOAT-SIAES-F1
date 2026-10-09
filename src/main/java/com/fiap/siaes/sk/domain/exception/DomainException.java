package com.fiap.siaes.sk.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public abstract class DomainException extends RuntimeException {

    private final transient HttpStatus status;
    private final transient Object[] parameters;

    protected DomainException(HttpStatus status, Object... parameters) {
        this.status = status;
        this.parameters = parameters;
    }

    public String getMessageKey() {
        return this.getClass().getSimpleName() + ".message";
    }

    public String getDetailKey() {
        return this.getClass().getSimpleName() + ".detail";
    }
}
