package com.fiap.siaes.customer.application.dto;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.infrastructure.persistence.projection.ListCustomerProjection;
import com.fiap.siaes.sk.document.domain.Document;
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

    public static CustomerResponse from(ListCustomerProjection customer) {
        Document document = Document.recreate(customer.getDocument());
        return new CustomerResponse(
                CustomerId.from(customer.getId()),
                document.getValue(),
                document.getDocumentType(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail());
    }
}
