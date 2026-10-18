package com.fiap.siaes.inventory.domain.repository;

import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface StockItemRepository extends RepositoryBase<StockItem, SuppliesId> {

    default StockItem findByIdOrThrowNotFound(SuppliesId suppliesId) {
        return this.findById(suppliesId)
                .orElseThrow(() -> new StockItemNotFoundException(suppliesId.toString()));
    }

    boolean existsById(SuppliesId suppliesId);

    default void existsByIdOrThrowNotFound(SuppliesId suppliesId) {
        if (!this.existsById(suppliesId)) {
            throw new StockItemNotFoundException(suppliesId.toString());
        }
    }
}
