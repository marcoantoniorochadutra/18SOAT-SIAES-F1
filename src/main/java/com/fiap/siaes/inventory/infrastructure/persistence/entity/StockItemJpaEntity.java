package com.fiap.siaes.inventory.infrastructure.persistence.entity;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_items")
public class StockItemJpaEntity {

    @Id
    @NotNull
    @Column(name = "supplies_id", updatable = false)
    private SuppliesId suppliesId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
