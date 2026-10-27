package com.fiap.siaes.quotation.infrastructure.notification;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import com.fiap.siaes.quotation.application.port.QuotationApprovalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "siaes.notification.whatsapp.enabled", havingValue = "true")
public class WhatsAppQuotationApprovalNotifier implements QuotationApprovalNotifier {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    public void send(QuotationApprovalNotification notification) {
        log.warn("WhatsApp provider is not integrated yet; the quotation approval link was not sent to the customer {}",
                 notification.customerName());
    }
}
