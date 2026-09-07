package com.fiap.siaes.quotation.domain.repository;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationId;

import java.util.List;
import java.util.Optional;

public interface QuotationRepository {

    Quotation save(Quotation quotation);

    Optional<Quotation> findById(QuotationId id);

    List<Quotation> findAll();

    List<Quotation> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);

    void deleteById(QuotationId id);
}
