package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderStatusTransitionInvalidException extends DomainException {

    public WorkOrderStatusTransitionInvalidException(String id, String currentStatus, String newStatus) {
        super(HttpStatus.CONFLICT, id, currentStatus, newStatus);
    }
}
