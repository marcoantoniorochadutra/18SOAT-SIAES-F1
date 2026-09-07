package com.fiap.siaes.supplies.infrastructure.persistence.repository;

import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliesJpaRepository extends JpaRepository<SuppliesJpaEntity, SuppliesId> {
}
