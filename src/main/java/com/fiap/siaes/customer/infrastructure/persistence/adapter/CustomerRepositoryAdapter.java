package com.fiap.siaes.customer.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.customer.infrastructure.persistence.mapper.CustomerMapper;
import com.fiap.siaes.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Customer save(Customer customer) {
        CustomerJpa entity = CustomerMapper.toEntity(customer);

        CustomerJpa saved = this.customerJpaRepository.save(entity);

        return CustomerMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return this.customerJpaRepository.findById(id).map(CustomerMapper::toDomain);
    }

    @Override
    public PageResponse<CustomerResponse> findAllByFilter(CustomerFilter filter, PageQuery pageQuery) {
        Slice<CustomerResponse> results = this.customerJpaRepository.findAllCustomers(filter.name(), filter.document(),
                                                                                      filter.email(), filter.phone(),
                                                                                      PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(CustomerResponse::from);

        return PageResponse.from(results);
    }

    @Override
    public boolean existsByDocument(Document document) {
        return this.customerJpaRepository.existsByDocument(document.getValue());
    }

    @Override
    public boolean existsByDocumentAndIdNot(String document, CustomerId id) {
        return this.customerJpaRepository.existsByDocumentAndIdNot(document, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.customerJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, CustomerId id) {
        return this.customerJpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(CustomerId id) {
        this.customerJpaRepository.deleteById(id);
    }
}
