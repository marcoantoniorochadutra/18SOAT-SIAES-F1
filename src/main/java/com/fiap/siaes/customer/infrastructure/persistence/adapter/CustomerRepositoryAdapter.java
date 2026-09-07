package com.fiap.siaes.customer.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.customer.infrastructure.persistence.mapper.CustomerMapper;
import com.fiap.siaes.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import com.fiap.siaes.sk.document.domain.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Customer save(Customer customer) {
        var entity = CustomerMapper.toEntity(customer);
        var saved = this.customerJpaRepository.save(entity);
        return CustomerMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return this.customerJpaRepository.findById(id).map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return this.customerJpaRepository.findAll().stream().map(CustomerMapper::toDomain).toList();
    }

    @Override
    public boolean existsByDocument(Document document) {
        return this.customerJpaRepository.existsByDocumentValue(document.value());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(CustomerId id) {
        this.customerJpaRepository.deleteById(id);
    }
}
