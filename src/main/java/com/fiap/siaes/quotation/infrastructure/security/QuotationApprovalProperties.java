package com.fiap.siaes.quotation.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "siaes.quotation.approval")
public record QuotationApprovalProperties(String secret, Duration timeToLive, String linkBaseUrl) {
}
