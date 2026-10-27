package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.event.QuotationApprovedEvent;
import com.fiap.siaes.quotation.domain.event.QuotationRejectedEvent;
import com.fiap.siaes.quotation.domain.exception.QuotationNotAwaitingApprovalException;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Quotation Decision")
class QuotationDecisionDomainTest {

    @Test
    @DisplayName("Deve aprovar o orçamento e registrar quem decidiu")
    void shouldApproveQuotation() {
        Quotation quotation = createQuotation();
        var decidedBy = UserId.generate();

        quotation.approve(QuotationDecisionChannel.CUSTOMER_ACCOUNT, decidedBy);

        assertEquals(QuotationStatus.APPROVED, quotation.getLastStatus());
        assertEquals(QuotationDecisionChannel.CUSTOMER_ACCOUNT, quotation.getDecisionChannel());
        assertEquals(decidedBy, quotation.getDecidedBy());
        assertNotNull(quotation.getDecidedAt());
        assertNull(quotation.getRejectionReason());
        assertEquals(2, quotation.getStatusHistory().size());

        var events = quotation.pullDomainEvents();
        assertEquals(1, events.size());
        var event = assertInstanceOf(QuotationApprovedEvent.class, events.getFirst());
        assertEquals(quotation.getId(), event.quotationId());
        assertEquals(quotation.getWorkOrderId(), event.workOrderId());
    }

    @Test
    @DisplayName("Deve rejeitar o orçamento guardando o motivo")
    void shouldRejectQuotation() {
        Quotation quotation = createQuotation();

        quotation.reject(QuotationDecisionChannel.APPROVAL_LINK, null, "  Muito caro  ");

        assertEquals(QuotationStatus.REJECTED, quotation.getLastStatus());
        assertEquals(QuotationDecisionChannel.APPROVAL_LINK, quotation.getDecisionChannel());
        assertNull(quotation.getDecidedBy());
        assertEquals("Muito caro", quotation.getRejectionReason());

        var events = quotation.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(QuotationRejectedEvent.class, events.getFirst());
    }

    @Test
    @DisplayName("Não deve decidir um orçamento que não aguarda aprovação")
    void shouldNotDecideQuotationNotAwaitingApproval() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.REJECTED);

        var exception = assertThrows(QuotationNotAwaitingApprovalException.class,
                                     () -> quotation.approve(QuotationDecisionChannel.APPROVAL_LINK, null));

        assertExceptionParameters(exception, quotation.getId().toString(), "REJECTED");
        assertEquals(QuotationStatus.REJECTED, quotation.getLastStatus());
        assertTrue(quotation.pullDomainEvents().isEmpty());
    }
}
