package com.fiap.siaes.maintenance.application.dto;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import lombok.Builder;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public record MaintenanceFilter(String code, String description, Boolean active) {

    @Builder
    public MaintenanceFilter {
        code = isBlank(code) ? null : MaintenanceCode.create(code).getValue();
        description = trimToNull(description);
        active = isNull(active) ? Boolean.TRUE : active;
    }
}
