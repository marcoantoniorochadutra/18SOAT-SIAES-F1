package com.fiap.siaes.inventory.infrastructure.persistence.entity;

import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private StockMovementId id;

    @NotNull
    @Column(name = "supplies_id", nullable = false, updatable = false)
    private SuppliesId suppliesId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 30)
    private StockMovementType type;

    @Column(name = "quantity", nullable = false, updatable = false)
    private int quantity;

    @Column(name = "stock_after", nullable = false, updatable = false)
    private int stockAfter;

    @Column(name = "reserved_after", nullable = false, updatable = false)
    private int reservedAfter;

    @Column(name = "reason", updatable = false, length = 255)
    private String reason;

    @Column(name = "reference_id", updatable = false)
    private UUID referenceId;

    @Column(name = "performed_by", updatable = false)
    private UserId performedBy;

    @NotNull
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;
}
