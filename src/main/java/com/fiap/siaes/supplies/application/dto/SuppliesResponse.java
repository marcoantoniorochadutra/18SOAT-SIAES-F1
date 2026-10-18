package com.fiap.siaes.supplies.application.dto;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

import java.math.BigDecimal;

public record SuppliesResponse(SuppliesId id, String sku, String description, BigDecimal unitPrice, boolean active) {

    public static SuppliesResponse from(Supplies supplies) {
        return new SuppliesResponse(
                supplies.getId(),
                supplies.getSku().getValue(),
                supplies.getDescription(),
                supplies.getUnitPrice(),
                supplies.isActive());
    }
}
