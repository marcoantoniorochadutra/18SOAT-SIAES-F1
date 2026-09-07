package com.fiap.siaes.customer.domain.repository;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    List<Customer> findAll();

    boolean existsByDocument(Document document);

    void deleteById(CustomerId id);
}
