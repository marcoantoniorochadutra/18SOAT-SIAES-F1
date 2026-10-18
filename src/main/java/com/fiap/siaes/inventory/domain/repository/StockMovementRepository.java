package com.fiap.siaes.inventory.domain.repository;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface StockMovementRepository extends RepositoryBase<StockMovement, StockMovementId> {

    PageResponse<StockMovementResponse> findAllBySuppliesId(SuppliesId suppliesId, PageQuery pageQuery);
}
