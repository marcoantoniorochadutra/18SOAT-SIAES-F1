package com.fiap.siaes.customer.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class CustomerJpa {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private CustomerId id;

    @NotNull
    @Column(name = "document", nullable = false, unique = true, length = 20)
    private String document;

    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email",unique = true, length = 150)
    private String email;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @JoinColumn(name = "customer_id")
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<VehicleJpaEntity> vehicles = new ArrayList<>();
}
