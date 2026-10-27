package com.fiap.siaes.customer.domain.model;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.exception.CustomerNameRequiredException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.domain.model.AggregateRoot;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class Customer extends AggregateRoot {

    private final CustomerId id;
    private Document document;
    private String name;
    private String phone;
    private String email;
    private final Instant createdAt;
    private Instant updatedAt;

    @Builder
    public Customer(CustomerId id, String document, String name, String phone, String email) {
        this.id = isNull(id) ? CustomerId.generate() : id;
        this.document = Document.create(document);
        this.name = this.validateName(name);
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
        String validName = this.validateName(name);
        Document validDocument = Document.create(document);

        this.name = validName;
        this.phone = phone;
        this.email = email;
        this.document = validDocument;
        this.updatedAt = Instant.now();
    }

    public void delete(UserId deletedBy) {
        this.registerEvent(CustomerDeletedEvent.of(this.id, deletedBy, Instant.now()));
    }

    private String validateName(String name) {
        if (isBlank(name)) {
            throw new CustomerNameRequiredException();
        }

        return name;
    }
}
