package com.fiap.siaes.customer.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UnitTest - Domain - Customer")
class CustomerDomainTest {

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void newCustomerStartsWithSameCreatedAndUpdatedAt() {
        Customer customer = Customer.builder()
                .id(CustomerId.generate())
                .document("529.982.247-25")
                .name("Maria")
                .email("email@email.com")
                .build();

        assertEquals("52998224725", customer.getDocument().getValue());
        assertEquals("Maria", customer.getName());
        assertEquals("email@email.com", customer.getEmail());
    }
}
