package com.fiap.siaes.customer.application.dto;

import lombok.Builder;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;


public record CustomerFilter(String name, String document, String email, String phone) {

    @Builder
    public CustomerFilter {
        name = trimToNull(name);
        document = isBlank(document) ? null : document.replaceAll("\\D", "");
        email = trimToNull(email);
        phone = trimToNull(phone);
    }
}
