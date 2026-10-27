package com.fiap.siaes.workorder.domain.exception;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.http.HttpStatus;

public class WorkOrderVehicleCustomerMismatchException extends DomainException {

    public WorkOrderVehicleCustomerMismatchException(String vehicleId, String customerId) {
        super(HttpStatus.BAD_REQUEST, vehicleId, customerId);
    }
}
