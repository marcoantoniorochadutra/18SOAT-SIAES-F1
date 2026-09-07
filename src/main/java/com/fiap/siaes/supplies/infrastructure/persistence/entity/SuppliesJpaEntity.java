package com.fiap.siaes.supplies.infrastructure.persistence.entity;

import com.fiap.siaes.supplies.domain.model.SuppliesId;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

//@Entity
//@Table(name = "suppliess")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuppliesJpaEntity {

    private SuppliesId id;

    private String description;
    private BigDecimal unitPrice;
    private int stockQuantity;
}
