package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationWorkOrderStatusInvalidException extends DomainException {

    public QuotationWorkOrderStatusInvalidException(String workOrderId, String status) {
        super(ErrorType.CONFLICT, workOrderId, status);
    }
}
