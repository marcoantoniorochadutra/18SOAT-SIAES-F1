package com.fiap.siaes.customer.infrastructure.persistence.mapper;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.customer.CustomerTestFactory.createCustomerJpa;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UnitTest - Mapper - Customer")
class CustomerMapperTest {


    @Test
    @DisplayName("Deve mapear Customer para CustomerJpa")
    void shouldMapCustomerToCustomerJpa() {
        Customer customer = createCustomer();

        CustomerJpa actualJpa = CustomerMapper.toEntity(customer);

        assertEquals(customer.getId(), actualJpa.getId());
        assertEquals(customer.getName(), actualJpa.getName());
        assertEquals(customer.getEmail(), actualJpa.getEmail());
        assertEquals(customer.getDocument().getValue(), actualJpa.getDocument());
        assertEquals(customer.getPhone(), actualJpa.getPhone());
    }

    @Test
    @DisplayName("Deve mapear CustomerJpa para Customer")
    void shouldMapCustomerJpaToCustomer() {
        CustomerJpa customerJpa = createCustomerJpa();

        Customer actualCustomer = CustomerMapper.toDomain(customerJpa);

        assertEquals(customerJpa.getId(), actualCustomer.getId());
        assertEquals(customerJpa.getName(), actualCustomer.getName());
        assertEquals(customerJpa.getEmail(), actualCustomer.getEmail());
        assertEquals(customerJpa.getDocument(), actualCustomer.getDocument().getValue());
        assertEquals(customerJpa.getPhone(), actualCustomer.getPhone());
    }

    @Test
    @DisplayName("Deve mapear para nulo quando customer não informado")
    void shouldMapToNullWhenCustomerIsNull() {
        CustomerJpa actualJpa = CustomerMapper.toEntity(null);
        assertEquals(null, actualJpa);
    }

    @Test
    @DisplayName("Deve mapear para nulo quando customerJpa não informado")
    void shouldMapToNullWhenCustomerJpaIsNull() {
        Customer actualCustomer = CustomerMapper.toDomain(null);
        assertEquals(null, actualCustomer);
    }
}