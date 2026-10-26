package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationAlreadyOpenException extends DomainException {

    public QuotationAlreadyOpenException(String workOrderId) {
        super(HttpStatus.CONFLICT, workOrderId);
    }
}
