package com.fiap.siaes.quotation.infrastructure.notification;

import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Notification - E-mail Quotation Approval")
class EmailQuotationApprovalNotifierTest {

    private static final String FROM = "oficina@siaes.com";
    private static final String RECIPIENT = "cliente@email.com";
    private static final String CUSTOMER_NAME = "João da Silva";
    private static final String LINK = "https://siaes.com/quotationApprovals/token";

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MessageSource i18nNotificationMessageSource;

    @InjectMocks
    private EmailQuotationApprovalNotifier emailQuotationApprovalNotifier;

    @Test
    @DisplayName("Deve atender o canal de e-mail")
    void shouldHandleEmailChannel() {
        assertEquals(NotificationChannel.EMAIL, this.emailQuotationApprovalNotifier.channel());
    }

    @Test
    @DisplayName("Deve enviar o e-mail com o link e a validade no fuso de São Paulo")
    void shouldSendEmailWithLinkAndExpiration() {
        ReflectionTestUtils.setField(this.emailQuotationApprovalNotifier, "from", FROM);
        var notification = QuotationApprovalNotification.of(RECIPIENT, CUSTOMER_NAME, LINK,
                                                            Instant.parse("2026-10-24T15:00:00Z"));

        when(this.i18nNotificationMessageSource.getMessage(eq("quotation.approval.email.subject"), any(), any(Locale.class)))
                .thenReturn("Aprovação de orçamento");
        when(this.i18nNotificationMessageSource.getMessage(eq("quotation.approval.email.body"),
                                                           eq(new Object[]{CUSTOMER_NAME, LINK, "24/10/2026 12:00"}),
                                                           any(Locale.class)))
                .thenReturn("Corpo do e-mail");

        this.emailQuotationApprovalNotifier.send(notification);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(this.javaMailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(FROM, message.getFrom());
        assertThat(message.getTo()).containsExactly(RECIPIENT);
        assertEquals("Aprovação de orçamento", message.getSubject());
        assertEquals("Corpo do e-mail", message.getText());
    }
}
