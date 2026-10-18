package com.fiap.siaes.maintenance.infrastructure.persistence.entity;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "maintenances")
public class MaintenanceJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private MaintenanceId id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotNull
    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private int estimatedDurationMinutes;

    @Column(name = "active", nullable = false)
    private boolean active;
}
