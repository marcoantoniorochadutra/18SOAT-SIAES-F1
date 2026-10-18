package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class StockAdjustmentReasonRequiredException extends DomainException {

    public StockAdjustmentReasonRequiredException(String suppliesId) {
        super(HttpStatus.BAD_REQUEST, suppliesId);
    }
}
