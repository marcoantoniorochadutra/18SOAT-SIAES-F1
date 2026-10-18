package com.fiap.siaes.maintenance;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class MaintenanceTestFactory {

    public static final String CODE = "TROCA-OLEO";
    public static final String DESCRIPTION = "Troca de óleo";
    public static final BigDecimal PRICE = new BigDecimal("80.00");
    public static final int ESTIMATED_DURATION_MINUTES = 30;

    public static Maintenance createMaintenance() {
        return createMaintenance(true);
    }

    public static Maintenance createMaintenance(boolean active) {
        return Maintenance.recreate()
                .id(MaintenanceId.generate())
                .code(MaintenanceCode.recreate(CODE))
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES)
                .active(active)
                .build();
    }

    public static MaintenanceJpaEntity createMaintenanceJpaEntity() {
        return MaintenanceJpaEntity.builder()
                .id(MaintenanceId.generate())
                .code(CODE)
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES)
                .active(true)
                .build();
    }

}
