package com.fiap.siaes.workorder.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
@Table(name = "work_orders")
public class WorkOrderJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private WorkOrderId id;

    @NotNull
    @Column(name = "customer_id", nullable = false, updatable = false)
    private CustomerId customerId;

    @NotNull
    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private VehicleId vehicleId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "last_status", nullable = false, length = 30)
    private WorkOrderStatus lastStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "work_order_id", nullable = false)
    private Set<WorkOrderStatusHistoryJpaEntity> statusHistory;

    @Column(name = "diagnosis", length = 2000)
    private String diagnosis;

    @Column(name = "diagnosed_by")
    private UserId diagnosedBy;

    @Column(name = "diagnosed_at")
    private Instant diagnosedAt;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
