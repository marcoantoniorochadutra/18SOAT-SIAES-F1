package com.fiap.siaes.quotation.infrastructure.notification;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import com.fiap.siaes.quotation.application.port.QuotationApprovalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WhatsAppQuotationApprovalNotifier implements QuotationApprovalNotifier {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    public void send(QuotationApprovalNotification notification) {
        // TODO integrate a WhatsApp provider; until then the workshop shares the link via POST /api/v1/quotations/{id}/approvalLink
        log.warn("WhatsApp integration is not configured; the quotation approval link was not sent to the customer {}",
                 notification.customerName());
    }
}
