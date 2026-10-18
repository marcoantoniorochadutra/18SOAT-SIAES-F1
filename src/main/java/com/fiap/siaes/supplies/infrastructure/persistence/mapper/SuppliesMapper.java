package com.fiap.siaes.supplies.infrastructure.persistence.mapper;

import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import com.fiap.siaes.supplies.infrastructure.persistence.projection.ListSuppliesProjection;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class SuppliesMapper {

    public static SuppliesJpaEntity toEntity(Supplies supplies) {
        if (isNull(supplies))
            return null;

        return SuppliesJpaEntity.builder()
                .id(supplies.getId())
                .sku(supplies.getSku().getValue())
                .description(supplies.getDescription())
                .unitPrice(supplies.getUnitPrice())
                .active(supplies.isActive())
                .build();
    }

    public static Supplies toDomain(SuppliesJpaEntity entity) {
        if (isNull(entity))
            return null;

        return Supplies.recreate()
                .id(entity.getId())
                .sku(Sku.recreate(entity.getSku()))
                .description(entity.getDescription())
                .unitPrice(entity.getUnitPrice())
                .active(entity.isActive())
                .build();
    }

    public static SuppliesResponse toResponse(ListSuppliesProjection projection) {
        return new SuppliesResponse(
                SuppliesId.from(projection.getId()),
                projection.getSku(),
                projection.getDescription(),
                projection.getUnitPrice(),
                projection.isActive());
    }
}
