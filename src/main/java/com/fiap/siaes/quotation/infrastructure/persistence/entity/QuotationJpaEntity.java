package com.fiap.siaes.quotation.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quotations")
public class QuotationJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private QuotationId id;

    @NotNull
    @Column(name = "work_order_id", nullable = false, updatable = false)
    private WorkOrderId workOrderId;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private CustomerId customerId;

    @NotNull
    @Column(name = "vehicle_id", nullable = false)
    private VehicleId vehicleId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "odometer", nullable = false)
    private long odometer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "last_status", nullable = false, length = 30)
    private QuotationStatus lastStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Set<QuotationStatusHistoryJpaEntity> statusHistory;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quotation_id", nullable = false)
    @OrderBy("description")
    private Set<QuotationSuppliesJpaEntity> supplies;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quotation_id", nullable = false)
    @OrderBy("description")
    private Set<QuotationMaintenanceJpaEntity> maintenances;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_channel", length = 30)
    private QuotationDecisionChannel decisionChannel;

    @Column(name = "decided_by")
    private UserId decidedBy;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
