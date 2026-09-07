package com.fiap.siaes.supplies.infrastructure.persistence.mapper;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;

public final class SuppliesMapper {

    private SuppliesMapper() {
    }

    public static SuppliesJpaEntity toEntity(Supplies supplies) {
        if (supplies == null) {
            return null;
        }
        return new SuppliesJpaEntity(supplies.getId(), supplies.getDescription(), supplies.getUnitPrice(), supplies.getStockQuantity());
    }

    public static Supplies toDomain(SuppliesJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Supplies.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .unitPrice(entity.getUnitPrice())
                .stockQuantity(entity.getStockQuantity())
                .build();
    }
}
