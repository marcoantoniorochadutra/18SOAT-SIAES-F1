package com.fiap.siaes.customer.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpa, CustomerId> {

    boolean existsByDocumentValue(String documentValue);
}
