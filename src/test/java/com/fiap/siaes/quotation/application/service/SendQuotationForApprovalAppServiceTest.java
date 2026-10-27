package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import com.fiap.siaes.quotation.application.port.QuotationApprovalNotifier;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase.SendQuotationForApprovalCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Send Quotation For Approval")
class SendQuotationForApprovalAppServiceTest {

    private static final String LINK = "https://siaes.com/quotationApprovals/token";

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Mock
    private QuotationApprovalNotifier emailNotifier;

    @Mock
    private QuotationApprovalNotifier whatsAppNotifier;

    @Spy
    private List<QuotationApprovalNotifier> quotationApprovalNotifiers = new ArrayList<>();

    @InjectMocks
    private SendQuotationForApprovalAppService sendQuotationForApprovalAppService;

    @Test
    @DisplayName("Deve enviar o mesmo link por e-mail e WhatsApp quando o cliente tiver os dois contatos")
    void shouldSendByEmailAndWhatsApp() {
        Customer customer = createCustomer();
        Quotation quotation = this.mockQuotationOf(customer);
        QuotationApprovalToken token = this.mockIssuedToken(quotation);
        this.mockNotifiers();
        when(this.emailNotifier.channel()).thenReturn(NotificationChannel.EMAIL);
        when(this.whatsAppNotifier.channel()).thenReturn(NotificationChannel.WHATSAPP);

        this.sendQuotationForApprovalAppService.execute(SendQuotationForApprovalCommand.of(quotation.getId()));

        verify(this.quotationApprovalTokenHelper).issue(quotation.getId());
        verify(this.emailNotifier).send(QuotationApprovalNotification.of(customer.getEmail(), customer.getName(), LINK, token.getExpiresAt()));
        verify(this.whatsAppNotifier).send(QuotationApprovalNotification.of(customer.getPhone(), customer.getName(), LINK, token.getExpiresAt()));
    }

    @Test
    @DisplayName("Deve enviar o link de aprovação somente por e-mail quando o cliente não tiver telefone")
    void shouldSendByEmailWithoutPhone() {
        Customer customer = this.customerWith("cliente@email.com", null);
        Quotation quotation = this.mockQuotationOf(customer);
        QuotationApprovalToken token = this.mockIssuedToken(quotation);
        this.mockNotifiers();
        when(this.emailNotifier.channel()).thenReturn(NotificationChannel.EMAIL);

        this.sendQuotationForApprovalAppService.execute(SendQuotationForApprovalCommand.of(quotation.getId()));

        verify(this.emailNotifier).send(QuotationApprovalNotification.of("cliente@email.com", customer.getName(), LINK, token.getExpiresAt()));
        verify(this.whatsAppNotifier, never()).send(any());
    }

    @Test
    @DisplayName("Deve enviar o link de aprovação pelo WhatsApp quando o cliente não tiver e-mail")
    void shouldSendByWhatsAppWithoutEmail() {
        Customer customer = this.customerWith(null, "11999999999");
        Quotation quotation = this.mockQuotationOf(customer);
        QuotationApprovalToken token = this.mockIssuedToken(quotation);
        this.mockNotifiers();
        when(this.whatsAppNotifier.channel()).thenReturn(NotificationChannel.WHATSAPP);

        this.sendQuotationForApprovalAppService.execute(SendQuotationForApprovalCommand.of(quotation.getId()));

        verify(this.whatsAppNotifier).send(QuotationApprovalNotification.of("11999999999", customer.getName(), LINK, token.getExpiresAt()));
        verify(this.emailNotifier, never()).send(any());
    }

    @Test
    @DisplayName("Não deve emitir link nem enviar quando o cliente não tiver contato")
    void shouldNotSendWithoutContact() {
        Customer customer = this.customerWith(null, " ");
        Quotation quotation = this.mockQuotationOf(customer);

        this.sendQuotationForApprovalAppService.execute(SendQuotationForApprovalCommand.of(quotation.getId()));

        verifyNoInteractions(this.quotationApprovalTokenHelper);
        verify(this.emailNotifier, never()).send(any());
        verify(this.whatsAppNotifier, never()).send(any());
    }

    @Test
    @DisplayName("Não deve enviar quando o orçamento já foi decidido")
    void shouldNotSendWhenQuotationAlreadyDecided() {
        Quotation quotation = createQuotation(createCustomer().getId(), QuotationStatus.APPROVED);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        this.sendQuotationForApprovalAppService.execute(SendQuotationForApprovalCommand.of(quotation.getId()));

        verifyNoInteractions(this.customerRepository, this.quotationApprovalTokenHelper);
    }

    private Quotation mockQuotationOf(Customer customer) {
        Quotation quotation = createQuotation(customer.getId(), QuotationStatus.AWAITING_APPROVAL);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        return quotation;
    }

    private QuotationApprovalToken mockIssuedToken(Quotation quotation) {
        QuotationApprovalToken token = createApprovalToken(quotation.getId());

        when(this.quotationApprovalTokenHelper.issue(quotation.getId())).thenReturn(token);
        when(this.quotationApprovalTokenHelper.linkFor(token)).thenReturn(LINK);

        return token;
    }

    private void mockNotifiers() {
        this.quotationApprovalNotifiers.addAll(List.of(this.emailNotifier, this.whatsAppNotifier));
    }

    private Customer customerWith(String email, String phone) {
        Customer customer = createCustomer();

        return Customer.recreate()
                .id(customer.getId())
                .document(customer.getDocument())
                .name(customer.getName())
                .email(email)
                .phone(phone)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
