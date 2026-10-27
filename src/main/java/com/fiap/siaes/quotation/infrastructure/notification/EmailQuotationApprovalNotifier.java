package com.fiap.siaes.quotation.infrastructure.notification;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import com.fiap.siaes.quotation.application.port.QuotationApprovalNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EmailQuotationApprovalNotifier implements QuotationApprovalNotifier {

    private static final DateTimeFormatter EXPIRATION_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("America/Sao_Paulo"));

    private final JavaMailSender javaMailSender;
    private final MessageSource i18nNotificationMessageSource;

    @Value("${siaes.mail.from}")
    private String from;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(QuotationApprovalNotification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.from);
        message.setTo(notification.recipient());
        message.setSubject(this.message("quotation.approval.email.subject"));
        message.setText(this.message("quotation.approval.email.body",
                                     notification.customerName(),
                                     notification.link(),
                                     EXPIRATION_FORMAT.format(notification.expiresAt())));

        this.javaMailSender.send(message);
    }

    private String message(String code, Object... args) {
        return this.i18nNotificationMessageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
