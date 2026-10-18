package com.fiap.siaes.supplies.domain.repository;

import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface SuppliesRepository extends RepositoryBase<Supplies, SuppliesId> {

    default Supplies findByIdOrThrowNotFound(SuppliesId id) {
        return this.findById(id)
                .orElseThrow(() -> new SuppliesNotFoundException(id.toString()));
    }

    boolean existsBySku(Sku sku);

    boolean existsBySkuAndIdNot(Sku sku, SuppliesId id);
}
