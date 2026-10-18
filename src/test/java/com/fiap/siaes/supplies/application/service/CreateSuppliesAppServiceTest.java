package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import com.fiap.siaes.supplies.domain.exception.SkuValueIsNullException;
import com.fiap.siaes.supplies.domain.exception.SuppliesSkuAlreadyExistsException;
import com.fiap.siaes.supplies.domain.exception.SuppliesUnitPriceInvalidException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.UNIT_PRICE;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Supplies")
class CreateSuppliesAppServiceTest {

    private static final String UNSANITIZED_SKU = " flt-oleo-001 ";

    @Mock
    private SuppliesRepository suppliesRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CreateSuppliesAppService createSuppliesAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve criar uma peça/insumo com sucesso")
    void shouldCreateSuppliesSuccessfully() {
        var command = this.commandBuilder().build();

        SuppliesId suppliesId = this.createSuppliesAppService.execute(command);

        Supplies savedSupplies = captureSave(this.suppliesRepository, Supplies.class);

        assertEquals(savedSupplies.getId(), suppliesId);
        assertEquals(SKU, savedSupplies.getSku().getValue());
        assertEquals(DESCRIPTION, savedSupplies.getDescription());
        assertEquals(UNIT_PRICE, savedSupplies.getUnitPrice());
        assertTrue(savedSupplies.isActive());
    }

    @Test
    @DisplayName("Deve publicar o evento de criação com o estoque inicial e o usuário autenticado")
    void shouldPublishCreatedEvent() {
        var authenticatedUser = UserTestFactory.authenticatedUserManager();
        var command = this.commandBuilder().authenticatedUser(authenticatedUser).build();

        SuppliesId suppliesId = this.createSuppliesAppService.execute(command);

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(SuppliesCreatedEvent.class, event -> {
            assertEquals(suppliesId, event.suppliesId());
            assertEquals(command.initialStock(), event.initialStock());
            assertEquals(authenticatedUser.id(), event.createdBy());
        });
    }

    @Test
    @DisplayName("Não deve criar peça/insumo quando o SKU já estiver cadastrado")
    void shouldNotCreateSuppliesWhenSkuAlreadyExists() {
        var command = this.commandBuilder().build();

        when(this.suppliesRepository.existsBySku(Sku.recreate(SKU))).thenReturn(true);

        var exception = assertThrows(SuppliesSkuAlreadyExistsException.class,
                () -> this.createSuppliesAppService.execute(command));

        assertExceptionParameters(exception, SKU);
        verify(this.suppliesRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve criar peça/insumo sem SKU")
    void shouldNotCreateSuppliesWithoutSku() {
        var command = this.commandBuilder().sku(" ").build();

        assertThrows(SkuValueIsNullException.class, () -> this.createSuppliesAppService.execute(command));

        verifyNoInteractions(this.suppliesRepository, this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve criar peça/insumo com preço unitário negativo")
    void shouldNotCreateSuppliesWithNegativeUnitPrice() {
        var command = this.commandBuilder().unitPrice(new BigDecimal("-1.00")).build();

        assertThrows(SuppliesUnitPriceInvalidException.class, () -> this.createSuppliesAppService.execute(command));

        verifyNoInteractions(this.suppliesRepository, this.domainEventPublisher);
    }

    private CreateSuppliesCommand.CreateSuppliesCommandBuilder commandBuilder() {
        return CreateSuppliesCommand.builder()
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .sku(UNSANITIZED_SKU)
                .description(DESCRIPTION)
                .unitPrice(UNIT_PRICE)
                .initialStock(10);
    }
}
