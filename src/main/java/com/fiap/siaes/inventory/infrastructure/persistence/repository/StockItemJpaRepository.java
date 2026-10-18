package com.fiap.siaes.inventory.infrastructure.persistence.repository;

import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemJpaRepository extends JpaRepository<StockItemJpaEntity, SuppliesId> {
}
