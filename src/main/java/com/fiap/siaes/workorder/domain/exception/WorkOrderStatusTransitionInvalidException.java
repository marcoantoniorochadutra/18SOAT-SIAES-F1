package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderStatusTransitionInvalidException extends DomainException {

    public WorkOrderStatusTransitionInvalidException(String id, String currentStatus, String newStatus) {
        super(ErrorType.CONFLICT, id, currentStatus, newStatus);
    }
}
