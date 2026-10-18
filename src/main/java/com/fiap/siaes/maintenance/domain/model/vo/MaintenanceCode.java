package com.fiap.siaes.maintenance.domain.model.vo;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeValueIsNullException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class MaintenanceCode {

    @EqualsAndHashCode.Include
    private final String value;

    private MaintenanceCode(String value) {
        if (isBlank(value)) {
            throw new MaintenanceCodeValueIsNullException();
        }

        this.value = this.sanitize(value);
    }

    public static MaintenanceCode create(String value) {
        return new MaintenanceCode(value);
    }

    public static MaintenanceCode recreate(String value) {
        return new MaintenanceCode(value);
    }

    private String sanitize(String value) {
        return value.trim().toUpperCase();
    }
}
