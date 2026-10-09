package com.fiap.siaes.customer.domain.repository;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface CustomerRepository extends RepositoryBase<Customer, CustomerId> {

    default Customer findByIdOrThrowNotFound(CustomerId id) {
        return this.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));
    }

    boolean existsByDocument(Document document);

    boolean existsByDocumentAndIdNot(String document, CustomerId id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, CustomerId id);

    void deleteById(CustomerId id);

    PageResponse<CustomerResponse> findAllByFilter(CustomerFilter filter, PageQuery pageQuery);
}
