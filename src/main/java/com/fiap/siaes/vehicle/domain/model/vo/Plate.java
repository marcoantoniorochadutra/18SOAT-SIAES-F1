package com.fiap.siaes.vehicle.domain.model.vo;

import com.fiap.siaes.vehicle.domain.exception.PlateValueIsNullException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Plate {

    @EqualsAndHashCode.Include
    private final String value;

    private Plate(String value) {
        if (isBlank(value)) {
            throw new PlateValueIsNullException();
        }

        this.value = this.sanitize(value);
    }

    public static Plate create(String value) {
        return new Plate(value);
    }

    public static Plate recreate(String value) {
        return new Plate(value);
    }

    private String sanitize(String value) {
        return value.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
}
