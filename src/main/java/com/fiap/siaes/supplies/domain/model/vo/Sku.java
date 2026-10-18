package com.fiap.siaes.supplies.domain.model.vo;

import com.fiap.siaes.supplies.domain.exception.SkuValueIsNullException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Sku {

    @EqualsAndHashCode.Include
    private final String value;

    private Sku(String value) {
        if (isBlank(value)) {
            throw new SkuValueIsNullException();
        }

        this.value = this.sanitize(value);
    }

    public static Sku create(String value) {
        return new Sku(value);
    }

    public static Sku recreate(String value) {
        return new Sku(value);
    }

    private String sanitize(String value) {
        return value.trim().toUpperCase();
    }
}
