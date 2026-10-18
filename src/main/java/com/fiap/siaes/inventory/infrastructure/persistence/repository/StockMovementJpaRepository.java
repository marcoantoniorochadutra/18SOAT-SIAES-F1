package com.fiap.siaes.inventory.infrastructure.persistence.repository;

import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, StockMovementId> {

    Slice<StockMovementJpaEntity> findAllBySuppliesIdOrderByOccurredAtDesc(SuppliesId suppliesId, Pageable pageable);
}
