package com.fiap.siaes.quotation.infrastructure.persistence.entity;

import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationMaintenanceId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "quotation_maintenances")
public class QuotationMaintenanceJpaEntity {

    @Id
    @Column(name = "id", updatable = false)
    private QuotationMaintenanceId id;

    @Column(name = "maintenance_id", nullable = false)
    private MaintenanceId maintenanceId;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
