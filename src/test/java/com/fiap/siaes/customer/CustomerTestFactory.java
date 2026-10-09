package com.fiap.siaes.customer;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.sk.document.domain.Document;
import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class CustomerTestFactory {

    public static Customer createCustomer() {
        return Customer.recreate()
                .id(CustomerId.generate())
                .name(Faker.instance().name().fullName())
                .email(Faker.instance().internet().emailAddress())
                .document(Document.recreate(Faker.instance().number().digits(11)))
                .phone(Faker.instance().phoneNumber().cellPhone())
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }

    public static CustomerJpa createCustomerJpa() {
        return CustomerJpa.builder()
                .id(CustomerId.generate())
                .name(Faker.instance().name().fullName())
                .email(Faker.instance().internet().emailAddress())
                .document(Faker.instance().number().digits(11))
                .phone(Faker.instance().phoneNumber().cellPhone())
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
    }

}
