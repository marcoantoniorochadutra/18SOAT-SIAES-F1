package com.fiap.siaes.sk.domain.exception;

import lombok.Getter;


@Getter
public abstract class DomainException extends RuntimeException {

    private final transient ErrorType type;
    private final transient Object[] parameters;

    protected DomainException(ErrorType type, Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public String getMessageKey() {
        return this.getClass().getSimpleName() + ".message";
    }

    public String getDetailKey() {
        return this.getClass().getSimpleName() + ".detail";
    }
}
