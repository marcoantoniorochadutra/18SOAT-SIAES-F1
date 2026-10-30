package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationNotAwaitingApprovalException extends DomainException {

    public QuotationNotAwaitingApprovalException(String id, String status) {
        super(ErrorType.CONFLICT, id, status);
    }
}
