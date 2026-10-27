package com.fiap.siaes.quotation.application.port;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;

public interface QuotationApprovalNotifier {

    NotificationChannel channel();

    void send(QuotationApprovalNotification notification);
}
