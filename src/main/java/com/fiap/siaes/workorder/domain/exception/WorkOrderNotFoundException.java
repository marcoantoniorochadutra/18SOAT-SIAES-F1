package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderNotFoundException extends DomainException {

    public WorkOrderNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, id);
    }
}
