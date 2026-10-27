package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase.ReserveStockCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InactiveSuppliesException;
import com.fiap.siaes.inventory.domain.exception.InsufficientStockException;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
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
import java.util.UUID;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Reserve Stock")
class ReserveStockAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private SuppliesRepository suppliesRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReserveStockAppService reserveStockAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve reservar o estoque de uma peça/insumo ativa e publicar a movimentação")
    void shouldReserveStockSuccessfully() {
        var stockItem = createStockItem();
        var command = ReserveStockCommand.of(stockItem.getSuppliesId(), 2, UUID.randomUUID(), UserId.generate());

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);
        when(this.suppliesRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(createSupplies(true));

        this.reserveStockAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(QUANTITY, savedStockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY + 2, savedStockItem.getReservedQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(StockMovementType.RESERVATION, event.type());
            assertEquals(2, event.quantity());
            assertEquals(command.referenceId(), event.referenceId());
            assertEquals(command.performedBy(), event.performedBy());
        });
    }

    @Test
    @DisplayName("Não deve reservar estoque de peça/insumo inativa")
    void shouldNotReserveStockWhenSuppliesIsInactive() {
        var stockItem = createStockItem();
        var command = ReserveStockCommand.of(stockItem.getSuppliesId(), 2, UUID.randomUUID(), UserId.generate());

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);
        when(this.suppliesRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(createSupplies(false));

        var exception = assertThrows(InactiveSuppliesException.class,
                () -> this.reserveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        assertEquals(RESERVED_QUANTITY, stockItem.getReservedQuantity());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve reservar estoque quando a peça/insumo não existir")
    void shouldNotReserveStockWhenSuppliesNotFound() {
        var stockItem = createStockItem();
        var command = ReserveStockCommand.of(stockItem.getSuppliesId(), 2, UUID.randomUUID(), UserId.generate());

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);
        when(this.suppliesRepository.findByIdOrThrowNotFound(command.suppliesId()))
                .thenThrow(new SuppliesNotFoundException(command.suppliesId().toString()));

        assertThrows(SuppliesNotFoundException.class, () -> this.reserveStockAppService.execute(command));

        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve reservar estoque inexistente")
    void shouldNotReserveStockWhenStockItemNotFound() {
        var command = ReserveStockCommand.of(SuppliesId.generate(), 2, UUID.randomUUID(), UserId.generate());

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId()))
                .thenThrow(new StockItemNotFoundException(command.suppliesId().toString()));

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.reserveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.suppliesRepository, this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve reservar quantidade maior que a disponível")
    void shouldNotReserveMoreThanAvailable() {
        var stockItem = createStockItem();
        int available = stockItem.getAvailableQuantity();
        var command = ReserveStockCommand.of(stockItem.getSuppliesId(), available + 1, UUID.randomUUID(), UserId.generate());

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);
        when(this.suppliesRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(createSupplies(true));

        var exception = assertThrows(InsufficientStockException.class,
                () -> this.reserveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), available, available + 1);
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }
}
