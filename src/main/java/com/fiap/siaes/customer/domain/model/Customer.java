package com.fiap.siaes.customer.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import static java.util.Objects.isNull;

@Getter
@Setter
public class Customer {

    private CustomerId id;
    private Document document;
    private String name;
    private String phone;
    private String email;

    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    @Builder
    public Customer(CustomerId id, String document, String name, String phone, String email) {
        this.id = isNull(id) ? CustomerId.generate() : id;
        this.document = Document.create(document);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public Customer(CustomerId id, Document document, String name, String phone, String email,
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(String name, String phone, String email, String document) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.document = Document.create(document);
        this.updatedAt = Instant.now();
    }
}
