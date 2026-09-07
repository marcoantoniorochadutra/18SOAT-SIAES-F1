package com.fiap.siaes.customer.domain.model;

import com.fiap.siaes.sk.document.domain.Document;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Customer {

    private CustomerId id;
    private Document document;
    private String name;
    private String phone;
    private String email;

    @Builder
    public Customer(CustomerId id, Document document, String name, String phone, String email) {
        this.id = isNull(id) ? CustomerId.generate() : id;
        this.document = document;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
