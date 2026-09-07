package com.fiap.siaes.maintenance.infrastructure.persistence.entity;

import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

//@Entity
//@Table(name = "service_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceJpaEntity {

    private MaintenanceId id;

    private String description;
    private BigDecimal price;
}
