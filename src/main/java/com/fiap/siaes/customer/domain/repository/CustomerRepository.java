package com.fiap.siaes.customer.domain.repository;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;

public interface CustomerRepository extends RepositoryBase<Customer, CustomerId> {

    default Customer findByIdOrThrowNotFound(CustomerId id) {
        return this.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));
    }

    boolean existsById(CustomerId id);

    default void existsByIdOrThrowNotFound(CustomerId id) {
        if (!this.existsById(id)) {
            throw new CustomerNotFoundException(id.toString());
        }
    }

    boolean existsByDocument(Document document);

    boolean existsByDocumentAndIdNot(Document document, CustomerId id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, CustomerId id);

    void deleteById(CustomerId id);
}
