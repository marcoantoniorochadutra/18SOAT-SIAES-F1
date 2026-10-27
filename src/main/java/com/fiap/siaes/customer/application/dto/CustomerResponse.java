package com.fiap.siaes.customer.application.dto;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import lombok.Builder;

@Builder
public record CustomerResponse(
        CustomerId id,
        String document,
        DocumentType documentType,
        String name,
        String phone,
        String email) {

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .document(customer.getDocument().getValue())
                .documentType(customer.getDocument().getDocumentType())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .build();
    }
}
