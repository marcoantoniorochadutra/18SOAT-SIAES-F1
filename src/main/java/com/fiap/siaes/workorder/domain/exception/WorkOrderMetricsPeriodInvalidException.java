package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderMetricsPeriodInvalidException extends DomainException {

    public WorkOrderMetricsPeriodInvalidException(String from, String to) {
        super(ErrorType.INVALID, from, to);
    }
}
