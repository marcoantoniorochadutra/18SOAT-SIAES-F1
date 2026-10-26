package com.fiap.siaes.quotation.application.dto;

import java.time.Instant;

public record QuotationApprovalNotification(String recipient, String customerName, String link, Instant expiresAt) {

    public static QuotationApprovalNotification of(String recipient, String customerName, String link, Instant expiresAt) {
        return new QuotationApprovalNotification(recipient, customerName, link, expiresAt);
    }
}
