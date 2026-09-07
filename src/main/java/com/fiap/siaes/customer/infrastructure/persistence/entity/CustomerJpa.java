package com.fiap.siaes.customer.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerJpa {

    private CustomerId id;

    private String documentValue;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String name;
    private String phone;
    private String email;
}
