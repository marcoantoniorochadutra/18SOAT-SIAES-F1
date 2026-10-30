package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.sk.domain.exception.ErrorType;

public class WorkOrderVehicleCustomerMismatchException extends DomainException {

    public WorkOrderVehicleCustomerMismatchException(String vehicleId, String customerId) {
        super(ErrorType.INVALID, vehicleId, customerId);
    }
}
