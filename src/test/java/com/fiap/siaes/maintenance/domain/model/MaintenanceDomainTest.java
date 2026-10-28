package com.fiap.siaes.maintenance.domain.model;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyActiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyInactiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeValueIsNullException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceDescriptionRequiredException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceEstimatedDurationInvalidException;
import com.fiap.siaes.maintenance.domain.exception.MaintenancePriceInvalidException;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.CODE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.DESCRIPTION;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.ESTIMATED_DURATION_MINUTES;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.PRICE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Maintenance")
class MaintenanceDomainTest {

    @Test
    @DisplayName("Deve criar um novo serviço ativo com sucesso")
    void shouldCreateMaintenanceSuccessfully() {
        Maintenance maintenance = Maintenance.builder()
                .code("troca-oleo")
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES)
                .build();

        assertNotNull(maintenance.getId());
        assertEquals(CODE, maintenance.getCode().getValue());
        assertEquals(DESCRIPTION, maintenance.getDescription());
        assertEquals(PRICE, maintenance.getPrice());
        assertEquals(ESTIMATED_DURATION_MINUTES, maintenance.getEstimatedDurationMinutes());
        assertTrue(maintenance.isActive());
    }

    @Test
    @DisplayName("Deve gerar um id diferente para cada novo serviço")
    void shouldGenerateDifferentIdForEachNewMaintenance() {
        Maintenance first = Maintenance.builder().code(CODE).description(DESCRIPTION).price(PRICE).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES).build();
        Maintenance second = Maintenance.builder().code(CODE).description(DESCRIPTION).price(PRICE).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES).build();

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    @DisplayName("Não deve criar serviço sem código")
    void shouldNotCreateMaintenanceWithoutCode() {
        var builder = Maintenance.builder().code(" ").price(PRICE).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);

        assertThrows(MaintenanceCodeValueIsNullException.class, builder::build);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Não deve criar serviço sem descrição")
    void shouldNotCreateMaintenanceWithoutDescription(String description) {
        var builder = Maintenance.builder().code(CODE).description(description).price(PRICE).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);

        assertThrows(MaintenanceDescriptionRequiredException.class, builder::build);
    }

    @Test
    @DisplayName("Deve remover os espaços das extremidades da descrição do serviço")
    void shouldTrimMaintenanceDescription() {
        Maintenance maintenance = Maintenance.builder().code(CODE).description("  " + DESCRIPTION + "  ").price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES).build();

        assertEquals(DESCRIPTION, maintenance.getDescription());
    }

    @Test
    @DisplayName("Não deve criar serviço sem preço")
    void shouldNotCreateMaintenanceWithoutPrice() {
        var builder = Maintenance.builder().code(CODE).description(DESCRIPTION).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);

        assertThrows(MaintenancePriceInvalidException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar serviço com preço negativo")
    void shouldNotCreateMaintenanceWithNegativePrice() {
        var builder = Maintenance.builder().code(CODE).description(DESCRIPTION).price(new BigDecimal("-0.01")).estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);

        assertThrows(MaintenancePriceInvalidException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve criar serviço com duração estimada zerada ou negativa")
    void shouldNotCreateMaintenanceWithInvalidEstimatedDuration() {
        var zero = Maintenance.builder().code(CODE).description(DESCRIPTION).price(PRICE).estimatedDurationMinutes(0);
        var negative = Maintenance.builder().code(CODE).description(DESCRIPTION).price(PRICE).estimatedDurationMinutes(-10);

        assertThrows(MaintenanceEstimatedDurationInvalidException.class, zero::build);
        assertThrows(MaintenanceEstimatedDurationInvalidException.class, negative::build);
    }

    @Test
    @DisplayName("Deve recriar um serviço mantendo os dados informados")
    void shouldRecreateMaintenanceKeepingGivenData() {
        var id = MaintenanceId.generate();

        Maintenance maintenance = Maintenance.recreate()
                .id(id)
                .code(MaintenanceCode.recreate(CODE))
                .description(DESCRIPTION)
                .price(PRICE)
                .active(false)
                .build();

        assertEquals(id, maintenance.getId());
        assertEquals(CODE, maintenance.getCode().getValue());
        assertEquals(DESCRIPTION, maintenance.getDescription());
        assertEquals(PRICE, maintenance.getPrice());
        assertFalse(maintenance.isActive());
    }

    @Test
    @DisplayName("Deve atualizar os dados do serviço mantendo id e situação")
    void shouldUpdateMaintenanceKeepingIdAndStatus() {
        Maintenance maintenance = createMaintenance();
        var id = maintenance.getId();

        maintenance.update("alinhamento", "Alinhamento", new BigDecimal("30.00"), 60);

        assertEquals(id, maintenance.getId());
        assertEquals("ALINHAMENTO", maintenance.getCode().getValue());
        assertEquals("Alinhamento", maintenance.getDescription());
        assertEquals(new BigDecimal("30.00"), maintenance.getPrice());
        assertEquals(60, maintenance.getEstimatedDurationMinutes());
        assertTrue(maintenance.isActive());
    }

    @Test
    @DisplayName("Não deve atualizar serviço sem código")
    void shouldNotUpdateMaintenanceWithoutCode() {
        Maintenance maintenance = createMaintenance();
        BigDecimal newPrice = new BigDecimal("30.00");

        assertThrows(MaintenanceCodeValueIsNullException.class,
                () -> maintenance.update(null, "Alinhamento", newPrice, 60));

        assertEquals(CODE, maintenance.getCode().getValue());
        assertEquals(DESCRIPTION, maintenance.getDescription());
        assertEquals(PRICE, maintenance.getPrice());
    }

    @Test
    @DisplayName("Não deve atualizar serviço com preço negativo")
    void shouldNotUpdateMaintenanceWithNegativePrice() {
        Maintenance maintenance = createMaintenance();
        BigDecimal negativePrice = new BigDecimal("-0.01");

        assertThrows(MaintenancePriceInvalidException.class,
                () -> maintenance.update("alinhamento", "Alinhamento", negativePrice, 60));

        assertEquals(CODE, maintenance.getCode().getValue());
        assertEquals(DESCRIPTION, maintenance.getDescription());
        assertEquals(PRICE, maintenance.getPrice());
    }

    @Test
    @DisplayName("Não deve atualizar serviço com duração estimada zerada")
    void shouldNotUpdateMaintenanceWithZeroEstimatedDuration() {
        Maintenance maintenance = createMaintenance();
        BigDecimal valor = new BigDecimal("30.00");

        assertThrows(MaintenanceEstimatedDurationInvalidException.class,
                () -> maintenance.update("alinhamento", "Alinhamento", valor, 0));

        assertEquals(CODE, maintenance.getCode().getValue());
        assertEquals(ESTIMATED_DURATION_MINUTES, maintenance.getEstimatedDurationMinutes());
    }

    @Test
    @DisplayName("Deve inativar um serviço ativo")
    void shouldDeactivateActiveMaintenance() {
        Maintenance maintenance = createMaintenance(true);

        maintenance.deactivate();

        assertFalse(maintenance.isActive());
    }

    @Test
    @DisplayName("Não deve inativar um serviço já inativo")
    void shouldNotDeactivateInactiveMaintenance() {
        Maintenance maintenance = createMaintenance(false);

        var exception = assertThrows(MaintenanceAlreadyInactiveException.class, maintenance::deactivate);

        assertExceptionParameters(exception, maintenance.getId().toString());
        assertFalse(maintenance.isActive());
    }

    @Test
    @DisplayName("Deve ativar um serviço inativo")
    void shouldActivateInactiveMaintenance() {
        Maintenance maintenance = createMaintenance(false);

        maintenance.activate();

        assertTrue(maintenance.isActive());
    }

    @Test
    @DisplayName("Não deve ativar um serviço já ativo")
    void shouldNotActivateActiveMaintenance() {
        Maintenance maintenance = createMaintenance(true);

        var exception = assertThrows(MaintenanceAlreadyActiveException.class, maintenance::activate);

        assertExceptionParameters(exception, maintenance.getId().toString());
        assertTrue(maintenance.isActive());
    }
}
