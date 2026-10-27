package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase.CreateStockItemCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InvalidStockQuantityException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Stock Item")
class CreateStockItemAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CreateStockItemAppService createStockItemAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve criar o estoque com a quantidade inicial e publicar a movimentação inicial")
    void shouldCreateStockItemWithInitialQuantity() {
        var command = CreateStockItemCommand.of(SuppliesId.generate(), QUANTITY, UserId.generate());

        this.createStockItemAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(command.suppliesId(), savedStockItem.getSuppliesId());
        assertEquals(QUANTITY, savedStockItem.getQuantity());
        assertEquals(0, savedStockItem.getReservedQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(command.suppliesId(), event.suppliesId());
            assertEquals(StockMovementType.INITIAL, event.type());
            assertEquals(QUANTITY, event.quantity());
            assertEquals(command.performedBy(), event.performedBy());
        });
    }

    @Test
    @DisplayName("Deve criar o estoque zerado sem publicar movimentação quando a quantidade inicial for zero")
    void shouldCreateEmptyStockItemWithoutMovement() {
        var command = CreateStockItemCommand.of(SuppliesId.generate(), 0, UserId.generate());

        this.createStockItemAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(0, savedStockItem.getQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).isEmpty();
    }

    @Test
    @DisplayName("Não deve criar o estoque com quantidade inicial negativa")
    void shouldNotCreateStockItemWithNegativeInitialQuantity() {
        var command = CreateStockItemCommand.of(SuppliesId.generate(), -1, UserId.generate());

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> this.createStockItemAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), -1);
        verifyNoInteractions(this.stockItemRepository, this.domainEventPublisher);
    }
}
