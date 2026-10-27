package com.fiap.siaes.supplies.application.dto;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SuppliesResponse(SuppliesId id, String sku, String description, BigDecimal unitPrice, boolean active) {

    public static SuppliesResponse from(Supplies supplies) {
        return SuppliesResponse.builder()
                .id(supplies.getId())
                .sku(supplies.getSku().getValue())
                .description(supplies.getDescription())
                .unitPrice(supplies.getUnitPrice())
                .active(supplies.isActive())
                .build();
    }
}
