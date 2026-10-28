package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderMetricsPeriodInvalidException extends DomainException {

    public WorkOrderMetricsPeriodInvalidException(String from, String to) {
        super(HttpStatus.BAD_REQUEST, from, to);
    }
}
