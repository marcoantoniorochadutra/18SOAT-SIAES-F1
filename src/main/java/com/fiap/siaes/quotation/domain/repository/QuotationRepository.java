package com.fiap.siaes.quotation.domain.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;

import java.util.List;

public interface QuotationRepository extends RepositoryBase<Quotation, QuotationId> {

    List<Quotation> findAll();

    List<Quotation> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);

    void deleteById(QuotationId id);
}
