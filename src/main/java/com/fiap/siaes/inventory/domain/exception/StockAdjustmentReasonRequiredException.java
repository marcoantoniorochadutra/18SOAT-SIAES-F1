package com.fiap.siaes.inventory.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class StockAdjustmentReasonRequiredException extends DomainException {

    public StockAdjustmentReasonRequiredException(String suppliesId) {
        super(ErrorType.INVALID, suppliesId);
    }
}
