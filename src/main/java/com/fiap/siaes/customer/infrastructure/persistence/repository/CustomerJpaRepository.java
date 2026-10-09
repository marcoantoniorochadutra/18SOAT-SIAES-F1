package com.fiap.siaes.customer.infrastructure.persistence.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.customer.infrastructure.persistence.projection.ListCustomerProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpa, CustomerId> {

    boolean existsByDocument(String document);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, CustomerId id);

    boolean existsByDocumentAndIdNot(String document, CustomerId id);


    @Query(value = """
            SELECT id, document, name, phone, email
            FROM {h-schema} customers
            WHERE (:name IS NULL OR name ILIKE '%' || :name || '%')
                AND (:document IS NULL OR document ILIKE '%' || :document || '%')
                AND (:email IS NULL OR email ILIKE '%' || :email || '%')
                AND (:phone IS NULL OR phone ILIKE '%' || :phone || '%')
            ORDER BY id
            """,
           nativeQuery = true)
    Slice<ListCustomerProjection> findAllCustomers(@Param("name") String name,
                                                   @Param("document") String document,
                                                   @Param("email") String email,
                                                   @Param("phone") String phone,
                                                   Pageable pageable);
}
