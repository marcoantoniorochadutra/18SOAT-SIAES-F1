package com.fiap.siaes.supplies.infrastructure.persistence.entity;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
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
@Table(name = "supplies")
public class SuppliesJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private SuppliesId id;

    @NotNull
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull
    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "active", nullable = false)
    private boolean active;
}
