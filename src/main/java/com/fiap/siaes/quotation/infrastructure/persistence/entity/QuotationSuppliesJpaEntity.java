package com.fiap.siaes.quotation.infrastructure.persistence.entity;

import com.fiap.siaes.quotation.domain.model.vo.QuotationSuppliesId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
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
@Table(name = "quotation_supplies")
public class QuotationSuppliesJpaEntity {

    @Id
    @Column(name = "id", updatable = false)
    private QuotationSuppliesId id;

    @Column(name = "supplies_id", nullable = false)
    private SuppliesId suppliesId;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
