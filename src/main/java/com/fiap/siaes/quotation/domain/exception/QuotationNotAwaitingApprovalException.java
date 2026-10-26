package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationNotAwaitingApprovalException extends DomainException {

    public QuotationNotAwaitingApprovalException(String id, String status) {
        super(HttpStatus.CONFLICT, id, status);
    }
}
