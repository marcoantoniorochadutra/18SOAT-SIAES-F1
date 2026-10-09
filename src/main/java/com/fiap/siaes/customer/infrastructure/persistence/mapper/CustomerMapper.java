package com.fiap.siaes.customer.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.sk.document.domain.Document;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class CustomerMapper {

    public static CustomerJpa toEntity(Customer customer) {
        if (isNull(customer))
            return null;

        return CustomerJpa.builder()
                .id(customer.getId())
                .document(customer.getDocument().getValue())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public static Customer toDomain(CustomerJpa entity) {
        if (isNull(entity))
            return null;

        return Customer.recreate()
                .id(entity.getId())
                .document(Document.recreate(entity.getDocument()))
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
