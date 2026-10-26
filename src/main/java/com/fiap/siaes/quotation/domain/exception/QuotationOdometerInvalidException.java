package com.fiap.siaes.quotation.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class QuotationOdometerInvalidException extends DomainException {

    public QuotationOdometerInvalidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
