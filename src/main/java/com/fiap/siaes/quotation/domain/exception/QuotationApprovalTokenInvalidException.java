package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class QuotationApprovalTokenInvalidException extends DomainException {

    public QuotationApprovalTokenInvalidException() {
        super(ErrorType.NOT_FOUND);
    }
}
