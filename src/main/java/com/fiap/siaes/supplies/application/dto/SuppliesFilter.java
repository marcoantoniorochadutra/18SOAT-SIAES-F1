package com.fiap.siaes.supplies.application.dto;

import com.fiap.siaes.supplies.domain.model.vo.Sku;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public record SuppliesFilter(String sku, String description, Boolean active) {

    public SuppliesFilter {
        sku = isBlank(sku) ? null : Sku.create(sku).getValue();
        description = trimToNull(description);
        active = isNull(active) ? Boolean.TRUE : active;
    }

    public static SuppliesFilter of(String sku, String description, Boolean active) {
        return new SuppliesFilter(sku, description, active);
    }
}
