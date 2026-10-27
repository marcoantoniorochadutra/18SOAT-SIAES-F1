package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationItemsRequiredException extends DomainException {

    public QuotationItemsRequiredException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
