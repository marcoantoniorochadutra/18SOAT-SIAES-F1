package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationApprovalTokenInvalidException extends DomainException {

    public QuotationApprovalTokenInvalidException() {
        super(HttpStatus.NOT_FOUND);
    }
}
