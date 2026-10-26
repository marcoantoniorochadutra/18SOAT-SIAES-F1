package com.fiap.siaes.supplies.application.dto;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public record SuppliesReferenceResponse(SuppliesId id, String sku, String description) {

    public static SuppliesReferenceResponse of(SuppliesId id, String sku, String description) {
        return new SuppliesReferenceResponse(id, sku, description);
    }

    public static SuppliesReferenceResponse from(Supplies supplies) {
        return SuppliesReferenceResponse.of(supplies.getId(), supplies.getSku().getValue(), supplies.getDescription());
    }
}
