package com.fiap.siaes.supplies.domain.model;

import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import com.fiap.siaes.supplies.domain.exception.SkuValueIsNullException;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyActiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyInactiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesUnitPriceInvalidException;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.UNIT_PRICE;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Supplies")
class SuppliesDomainTest {

    @Test
    @DisplayName("Deve criar uma nova peça/insumo ativa com sucesso")
    void shouldCreateSuppliesSuccessfully() {
        Supplies supplies = Supplies.builder()
                .sku("flt-oleo-001")
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .initialStock(10)
                .createdBy(UserId.generate())
                .build();

        assertNotNull(supplies.getId());
        assertEquals(SKU, supplies.getSku().getValue());
        assertEquals(DESCRIPTION, supplies.getDescription());
        assertEquals(UNIT_PRICE, supplies.getUnitPrice());
        assertTrue(supplies.isActive());
    }

    @Test
    @DisplayName("Deve registrar o evento de criação com o estoque inicial e o usuário criador")
    void shouldRegisterCreatedEvent() {
        var createdBy = UserId.generate();

        Supplies supplies = Supplies.builder()
                .sku(SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .initialStock(10)
                .createdBy(createdBy)
                .build();

        var events = supplies.pullDomainEvents();

        assertThat(events).singleElement().isInstanceOfSatisfying(SuppliesCreatedEvent.class, event -> {
            assertEquals(supplies.getId(), event.suppliesId());
            assertEquals(10, event.initialStock());
            assertEquals(createdBy, event.createdBy());
            assertNotNull(event.occurredAt());
        });
        assertThat(supplies.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve gerar um id diferente para cada nova peça/insumo")
    void shouldGenerateDifferentIdForEachNewSupplies() {
        Supplies first = Supplies.builder().sku(SKU).unitPrice(UNIT_PRICE).build();
        Supplies second = Supplies.builder().sku(SKU).unitPrice(UNIT_PRICE).build();

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    @DisplayName("Não deve criar peça/insumo sem SKU")
    void shouldNotCreateSuppliesWithoutSku() {
        var builder = Supplies.builder().sku(" ").unitPrice(UNIT_PRICE);

        assertThrows(SkuValueIsNullException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar peça/insumo sem preço unitário")
    void shouldNotCreateSuppliesWithoutUnitPrice() {
        var builder = Supplies.builder().sku(SKU);

        assertThrows(SuppliesUnitPriceInvalidException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar peça/insumo com preço unitário negativo")
    void shouldNotCreateSuppliesWithNegativeUnitPrice() {
        var builder = Supplies.builder().sku(SKU).unitPrice(new BigDecimal("-0.01"));

        assertThrows(SuppliesUnitPriceInvalidException.class, builder::build);
    }

    @Test
    @DisplayName("Deve recriar uma peça/insumo mantendo os dados informados sem registrar eventos")
    void shouldRecreateSuppliesKeepingGivenData() {
        var id = SuppliesId.generate();

        Supplies supplies = Supplies.recreate()
                .id(id)
                .sku(Sku.recreate(SKU))
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .active(false)
                .build();

        assertEquals(id, supplies.getId());
        assertEquals(SKU, supplies.getSku().getValue());
        assertEquals(DESCRIPTION, supplies.getDescription());
        assertEquals(UNIT_PRICE, supplies.getUnitPrice());
        assertFalse(supplies.isActive());
        assertThat(supplies.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar os dados da peça/insumo mantendo id e situação")
    void shouldUpdateSuppliesKeepingIdAndStatus() {
        Supplies supplies = createSupplies();
        var id = supplies.getId();

        supplies.update("flt-ar-002", "Filtro de ar", new BigDecimal("30.00"));

        assertEquals(id, supplies.getId());
        assertEquals("FLT-AR-002", supplies.getSku().getValue());
        assertEquals("Filtro de ar", supplies.getDescription());
        assertEquals(new BigDecimal("30.00"), supplies.getUnitPrice());
        assertTrue(supplies.isActive());
    }

    @Test
    @DisplayName("Não deve atualizar peça/insumo sem SKU")
    void shouldNotUpdateSuppliesWithoutSku() {
        Supplies supplies = createSupplies();
        BigDecimal newUnitPrice = new BigDecimal("30.00");

        assertThrows(SkuValueIsNullException.class,
                () -> supplies.update(null, "Filtro de ar", newUnitPrice));

        assertEquals(SKU, supplies.getSku().getValue());
        assertEquals(DESCRIPTION, supplies.getDescription());
        assertEquals(UNIT_PRICE, supplies.getUnitPrice());
    }

    @Test
    @DisplayName("Não deve atualizar peça/insumo com preço unitário negativo")
    void shouldNotUpdateSuppliesWithNegativeUnitPrice() {
        Supplies supplies = createSupplies();
        BigDecimal negativeUnitPrice = new BigDecimal("-0.01");

        assertThrows(SuppliesUnitPriceInvalidException.class,
                () -> supplies.update("flt-ar-002", "Filtro de ar", negativeUnitPrice));

        assertEquals(SKU, supplies.getSku().getValue());
        assertEquals(DESCRIPTION, supplies.getDescription());
        assertEquals(UNIT_PRICE, supplies.getUnitPrice());
    }

    @Test
    @DisplayName("Deve inativar uma peça/insumo ativa")
    void shouldDeactivateActiveSupplies() {
        Supplies supplies = createSupplies(true);

        supplies.deactivate();

        assertFalse(supplies.isActive());
    }

    @Test
    @DisplayName("Não deve inativar uma peça/insumo já inativa")
    void shouldNotDeactivateInactiveSupplies() {
        Supplies supplies = createSupplies(false);

        var exception = assertThrows(SuppliesAlreadyInactiveException.class, supplies::deactivate);

        assertExceptionParameters(exception, supplies.getId().toString());
        assertFalse(supplies.isActive());
    }

    @Test
    @DisplayName("Deve ativar uma peça/insumo inativa")
    void shouldActivateInactiveSupplies() {
        Supplies supplies = createSupplies(false);

        supplies.activate();

        assertTrue(supplies.isActive());
    }

    @Test
    @DisplayName("Não deve ativar uma peça/insumo já ativa")
    void shouldNotActivateActiveSupplies() {
        Supplies supplies = createSupplies(true);

        var exception = assertThrows(SuppliesAlreadyActiveException.class, supplies::activate);

        assertExceptionParameters(exception, supplies.getId().toString());
        assertTrue(supplies.isActive());
    }
}
