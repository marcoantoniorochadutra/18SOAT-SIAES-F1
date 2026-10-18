package com.fiap.siaes.supplies;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class SuppliesTestFactory {

    public static final String SKU = "FLT-OLEO-001";
    public static final String DESCRIPTION = "Filtro de óleo";
    public static final BigDecimal UNIT_PRICE = new BigDecimal("45.90");

    public static Supplies createSupplies() {
        return createSupplies(true);
    }

    public static Supplies createSupplies(boolean active) {
        return Supplies.recreate()
                .id(SuppliesId.generate())
                .sku(Sku.recreate(SKU))
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .active(active)
                .build();
    }

    public static SuppliesJpaEntity createSuppliesJpaEntity() {
        return SuppliesJpaEntity.builder()
                .id(SuppliesId.generate())
                .sku(SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .active(true)
                .build();
    }

}
