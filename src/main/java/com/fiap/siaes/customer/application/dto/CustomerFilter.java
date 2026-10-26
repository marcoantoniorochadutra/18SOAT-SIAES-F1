package com.fiap.siaes.customer.application.dto;

import com.fiap.siaes.sk.document.domain.Document;

import static org.apache.commons.lang3.StringUtils.trimToNull;


public record CustomerFilter(String name, String document, String email, String phone) {

    public CustomerFilter {
        name = trimToNull(name);
        document = Document.sanitize(document);
        email = trimToNull(email);
        phone = trimToNull(phone);
    }

    public static CustomerFilter of(String name, String document, String email, String phone) {
        return new CustomerFilter(name, document, email, phone);
    }
}
