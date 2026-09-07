package com.fiap.siaes.quotation.application.usecase.diagnosis.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diagnosiss")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisJpaEntity {

    @Id
    private DiagnosisId id;

    private CustomerId ownerId;
    private String plate;
    private String brand;
    private String model;
    private int year;
}
