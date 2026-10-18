package com.fiap.siaes.inventory.infrastructure.persistence.mapper;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class StockItemMapper {

    public static StockItemJpaEntity toEntity(StockItem stockItem) {
        if (isNull(stockItem))
            return null;

        return StockItemJpaEntity.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(stockItem.getQuantity())
                .reservedQuantity(stockItem.getReservedQuantity())
                .version(stockItem.getVersion())
                .build();
    }

    public static StockItem toDomain(StockItemJpaEntity entity) {
        if (isNull(entity))
            return null;

        return StockItem.recreate()
                .suppliesId(entity.getSuppliesId())
                .quantity(entity.getQuantity())
                .reservedQuantity(entity.getReservedQuantity())
                .version(entity.getVersion())
                .build();
    }
}
