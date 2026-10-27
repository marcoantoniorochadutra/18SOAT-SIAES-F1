package com.fiap.siaes.vehicle.domain.model.vo;

import com.fiap.siaes.vehicle.domain.exception.PlateInvalidException;
import com.fiap.siaes.vehicle.domain.exception.PlateValueIsNullException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Plate {

    private static final Pattern FORMAT = Pattern.compile("^[A-Z]{3}\\d[A-Z0-9]\\d{2}$");

    @EqualsAndHashCode.Include
    private final String value;

    private Plate(String value) {
        this.value = value;
    }

    public static Plate create(String value) {
        if (isBlank(value)) {
            throw new PlateValueIsNullException();
        }

        String plate = sanitize(value);

        if (!FORMAT.matcher(plate).matches()) {
            throw new PlateInvalidException(value);
        }

        return new Plate(plate);
    }

    public static Plate recreate(String value) {
        return new Plate(value);
    }

    public static String sanitize(String value) {
        return isBlank(value) ? null : value.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
}
