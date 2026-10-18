package com.fiap.siaes.customer.application.dto;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;

public record CustomerResponse(
        CustomerId id,
        String document,
        DocumentType documentType,
        String name,
        String phone,
        String email) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getDocument().getValue(),
                customer.getDocument().getDocumentType(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail());
    }
}
