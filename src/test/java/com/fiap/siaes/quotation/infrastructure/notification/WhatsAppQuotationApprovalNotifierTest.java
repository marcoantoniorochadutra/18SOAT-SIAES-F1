package com.fiap.siaes.quotation.infrastructure.notification;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UnitTest - Notification - WhatsApp Quotation Approval")
class WhatsAppQuotationApprovalNotifierTest {

    private final WhatsAppQuotationApprovalNotifier whatsAppQuotationApprovalNotifier = new WhatsAppQuotationApprovalNotifier();

    @Test
    @DisplayName("Deve atender o canal de WhatsApp")
    void shouldHandleWhatsAppChannel() {
        assertEquals(NotificationChannel.WHATSAPP, this.whatsAppQuotationApprovalNotifier.channel());
    }

    @Test
    @DisplayName("Não deve falhar enquanto a integração com o WhatsApp não estiver configurada")
    void shouldNotFailWhileIntegrationIsNotConfigured() {
        var notification = QuotationApprovalNotification.of("11999999999", "João da Silva",
                                                            "https://siaes.com/quotationApprovals/token", Instant.now());

        assertDoesNotThrow(() -> this.whatsAppQuotationApprovalNotifier.send(notification));
    }
}
