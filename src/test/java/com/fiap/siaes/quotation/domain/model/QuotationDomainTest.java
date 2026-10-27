package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.quotation.domain.exception.QuotationDuplicatedItemException;
import com.fiap.siaes.quotation.domain.exception.QuotationItemQuantityInvalidException;
import com.fiap.siaes.quotation.domain.exception.QuotationItemsRequiredException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotEditableException;
import com.fiap.siaes.quotation.domain.exception.QuotationOdometerInvalidException;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.quotation.QuotationTestFactory.ODOMETER;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotationMaintenance;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotationSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Quotation")
class QuotationDomainTest {

    @Test
    @DisplayName("Deve remover espaços da descrição do orçamento e tratar descrição em branco como ausente")
    void shouldNormalizeQuotationDescription() {
        Quotation quotation = this.quotationBuilder().description("  Revisão  ")
                .supplies(List.of(createQuotationSupplies(SuppliesId.generate(), 1)))
                .build();
        Quotation blank = this.quotationBuilder().description("   ")
                .supplies(List.of(createQuotationSupplies(SuppliesId.generate(), 1)))
                .build();

        assertEquals("Revisão", quotation.getDescription());
        assertNull(blank.getDescription());
    }

    @Test
    @DisplayName("Não deve permitir alterar os itens do orçamento por fora do agregado")
    void shouldNotAllowExternalChangesToItems() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);
        var supplies = quotation.getSupplies();

        assertThrows(UnsupportedOperationException.class, supplies::clear);
    }

    @Test
    @DisplayName("Deve criar um orçamento em rascunho com histórico de status e sem eventos")
    void shouldCreateDraftQuotation() {
        var suppliesId = SuppliesId.generate();

        Quotation quotation = this.quotationBuilder()
                .supplies(List.of(createQuotationSupplies(suppliesId, 2)))
                .maintenances(List.of(createQuotationMaintenance(MaintenanceId.generate(), 1)))
                .build();

        assertNotNull(quotation.getId());
        assertNotNull(quotation.getCreatedAt());
        assertEquals(QuotationStatus.DRAFT, quotation.getLastStatus());
        assertEquals(1, quotation.getStatusHistory().size());
        assertEquals(QuotationStatus.DRAFT, quotation.getStatusHistory().iterator().next().getStatus());
        assertEquals(new BigDecimal("171.80"), quotation.getTotalAmount());
        assertEquals(Map.of(suppliesId, 2), quotation.getSuppliesQuantities());
        assertTrue(quotation.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve concluir o orçamento em rascunho e registrar o evento de orçamento concluído")
    void shouldCompleteDraftQuotation() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);

        quotation.complete();

        assertEquals(QuotationStatus.AWAITING_APPROVAL, quotation.getLastStatus());
        assertEquals(2, quotation.getStatusHistory().size());

        var events = quotation.pullDomainEvents();

        assertEquals(1, events.size());
        var event = assertInstanceOf(QuotationCompletedEvent.class, events.getFirst());
        assertEquals(quotation.getId(), event.quotationId());
        assertEquals(quotation.getWorkOrderId(), event.workOrderId());
        assertNotNull(event.occurredAt());
        assertTrue(quotation.pullDomainEvents().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = QuotationStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve concluir orçamento que não está em rascunho")
    void shouldNotCompleteQuotationNotDraft(QuotationStatus status) {
        Quotation quotation = createQuotation(CustomerId.generate(), status);

        var exception = assertThrows(QuotationNotEditableException.class, quotation::complete);

        assertExceptionParameters(exception, quotation.getId().toString(), status.name());
        assertEquals(status, quotation.getLastStatus());
        assertTrue(quotation.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve descartar o orçamento em rascunho")
    void shouldDiscardDraftQuotation() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);

        quotation.discard();

        assertEquals(QuotationStatus.DISCARDED, quotation.getLastStatus());
        assertEquals(2, quotation.getStatusHistory().size());
        assertTrue(quotation.pullDomainEvents().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = QuotationStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve descartar orçamento que não está em rascunho")
    void shouldNotDiscardQuotationNotDraft(QuotationStatus status) {
        Quotation quotation = createQuotation(CustomerId.generate(), status);

        assertThrows(QuotationNotEditableException.class, quotation::discard);

        assertEquals(status, quotation.getLastStatus());
    }

    @Test
    @DisplayName("Não deve criar orçamento sem itens")
    void shouldNotCreateQuotationWithoutItems() {
        var builder = this.quotationBuilder().supplies(List.of()).maintenances(null);

        assertThrows(QuotationItemsRequiredException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar orçamento com insumo duplicado")
    void shouldNotCreateQuotationWithDuplicatedSupplies() {
        var suppliesId = SuppliesId.generate();
        var builder = this.quotationBuilder()
                .supplies(List.of(createQuotationSupplies(suppliesId, 1), createQuotationSupplies(suppliesId, 2)));

        var exception = assertThrows(QuotationDuplicatedItemException.class, builder::build);

        assertExceptionParameters(exception, suppliesId.toString());
    }

    @Test
    @DisplayName("Não deve criar orçamento com quilometragem negativa")
    void shouldNotCreateQuotationWithNegativeOdometer() {
        var builder = this.quotationBuilder()
                .odometer(-1)
                .supplies(List.of(createQuotationSupplies(SuppliesId.generate(), 1)));

        assertThrows(QuotationOdometerInvalidException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar item de orçamento com quantidade menor ou igual a zero")
    void shouldNotCreateItemWithInvalidQuantity() {
        var suppliesId = SuppliesId.generate();
        var maintenanceId = MaintenanceId.generate();

        assertThrows(QuotationItemQuantityInvalidException.class, () -> createQuotationSupplies(suppliesId, 0));
        assertThrows(QuotationItemQuantityInvalidException.class, () -> createQuotationMaintenance(maintenanceId, 0));
    }

    @Test
    @DisplayName("Deve atualizar orçamento em rascunho")
    void shouldUpdateDraftQuotation() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);
        var maintenanceId = MaintenanceId.generate();

        quotation.update("Nova descrição", 51_000L, List.of(), List.of(createQuotationMaintenance(maintenanceId, 2)));

        assertEquals("Nova descrição", quotation.getDescription());
        assertEquals(51_000L, quotation.getOdometer());
        assertEquals(0, quotation.getSupplies().size());
        assertEquals(maintenanceId, quotation.getMaintenances().getFirst().getMaintenanceId());
        assertEquals(Map.of(), quotation.getSuppliesQuantities());
    }

    @ParameterizedTest
    @EnumSource(value = QuotationStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve atualizar orçamento que não está em rascunho")
    void shouldNotUpdateQuotationNotDraft(QuotationStatus status) {
        Quotation quotation = createQuotation(CustomerId.generate(), status);
        var supplies = List.of(createQuotationSupplies(SuppliesId.generate(), 1));

        var exception = assertThrows(QuotationNotEditableException.class,
                () -> quotation.update(DESCRIPTION, ODOMETER, supplies, List.of()));

        assertExceptionParameters(exception, quotation.getId().toString(), status.name());
    }

    private Quotation.QuotationBuilder quotationBuilder() {
        return Quotation.builder()
                .workOrderId(WorkOrderId.generate())
                .customerId(CustomerId.generate())
                .vehicleId(VehicleId.generate())
                .description(DESCRIPTION)
                .odometer(ODOMETER);
    }
}
