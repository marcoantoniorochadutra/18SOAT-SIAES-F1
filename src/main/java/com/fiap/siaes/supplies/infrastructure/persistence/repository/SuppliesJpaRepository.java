package com.fiap.siaes.supplies.infrastructure.persistence.repository;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import com.fiap.siaes.supplies.infrastructure.persistence.projection.ListSuppliesProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuppliesJpaRepository extends JpaRepository<SuppliesJpaEntity, SuppliesId> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, SuppliesId id);

    @Query(value = """
            SELECT
                s.id AS id,
                s.sku AS sku,
                s.description AS description,
                s.unit_price AS unitPrice,
                s.active AS active
            FROM {h-schema} supplies s
            WHERE (:sku IS NULL OR s.sku ILIKE '%' || :sku || '%')
                AND (:description IS NULL OR s.description ILIKE '%' || :description || '%')
                AND s.active = :active
            ORDER BY s.description
            """,
           nativeQuery = true)
    Slice<ListSuppliesProjection> findAllSupplies(@Param("sku") String sku,
                                                  @Param("description") String description,
                                                  @Param("active") boolean active,
                                                  Pageable pageable);
}
