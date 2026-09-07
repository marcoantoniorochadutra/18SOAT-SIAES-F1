package com.fiap.siaes.customer.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.sk.document.domain.Document;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerJpa toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerJpa(
                customer.getId(),
                customer.getDocument().value(),
                customer.getDocument().type(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail()
        );
    }

    public static Customer toDomain(CustomerJpa entity) {
        if (entity == null) {
            return null;
        }
        return Customer.builder()
                .id(entity.getId())
                .document(new Document(entity.getDocumentValue(), entity.getDocumentType()))
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .build();
    }
}
