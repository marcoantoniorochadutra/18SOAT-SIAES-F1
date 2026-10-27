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
        this.value = value;
    }

    public static Sku create(String value) {
        if (isBlank(value)) {
            throw new SkuValueIsNullException();
        }

        return new Sku(value.trim().toUpperCase());
    }

    public static Sku recreate(String value) {
        return new Sku(value);
    }
}
