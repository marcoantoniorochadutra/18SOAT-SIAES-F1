package com.fiap.siaes.customer.application.dto;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;

public record CustomerReferenceResponse(CustomerId id, String name) {

    public static CustomerReferenceResponse of(CustomerId id, String name) {
        return new CustomerReferenceResponse(id, name);
    }

    public static CustomerReferenceResponse from(Customer customer) {
        return CustomerReferenceResponse.of(customer.getId(), customer.getName());
    }
}
