package com.fiap.siaes.inventory.infrastructure.persistence.mapper;

import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class StockMovementMapper {

    public static StockMovementJpaEntity toEntity(StockMovement stockMovement) {
        if (isNull(stockMovement))
            return null;

        return StockMovementJpaEntity.builder()
                .id(stockMovement.getId())
                .suppliesId(stockMovement.getSuppliesId())
                .type(stockMovement.getType())
                .quantity(stockMovement.getQuantity())
                .stockAfter(stockMovement.getStockAfter())
                .reservedAfter(stockMovement.getReservedAfter())
                .reason(stockMovement.getReason())
                .referenceId(stockMovement.getReferenceId())
                .performedBy(stockMovement.getPerformedBy())
                .occurredAt(stockMovement.getOccurredAt())
                .build();
    }

    public static StockMovement toDomain(StockMovementJpaEntity entity) {
        if (isNull(entity))
            return null;

        return StockMovement.recreate()
                .id(entity.getId())
                .suppliesId(entity.getSuppliesId())
                .type(entity.getType())
                .quantity(entity.getQuantity())
                .stockAfter(entity.getStockAfter())
                .reservedAfter(entity.getReservedAfter())
                .reason(entity.getReason())
                .referenceId(entity.getReferenceId())
                .performedBy(entity.getPerformedBy())
                .occurredAt(entity.getOccurredAt())
                .build();
    }
}
