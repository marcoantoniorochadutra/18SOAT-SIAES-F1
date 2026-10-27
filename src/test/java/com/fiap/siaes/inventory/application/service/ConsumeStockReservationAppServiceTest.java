package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.ConsumeStockReservationUseCase.ConsumeStockReservationCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InvalidStockQuantityException;
import com.fiap.siaes.inventory.domain.exception.ReservedStockExceededException;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
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
import java.util.UUID;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
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
@DisplayName("UnitTest - App Service - Consume Stock Reservation")
class ConsumeStockReservationAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ConsumeStockReservationAppService consumeStockReservationAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve consumir a reserva baixando o estoque e publicar a movimentação")
    void shouldConsumeReservationSuccessfully() {
        var stockItem = createStockItem();
        var command = ConsumeStockReservationCommand.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(RESERVED_QUANTITY)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        this.consumeStockReservationAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(QUANTITY - RESERVED_QUANTITY, savedStockItem.getQuantity());
        assertEquals(0, savedStockItem.getReservedQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(StockMovementType.CONSUMPTION, event.type());
            assertEquals(RESERVED_QUANTITY, event.quantity());
            assertEquals(QUANTITY - RESERVED_QUANTITY, event.stockAfter());
            assertEquals(0, event.reservedAfter());
            assertEquals(command.referenceId(), event.referenceId());
            assertEquals(command.performedBy(), event.performedBy());
        });
    }

    @Test
    @DisplayName("Não deve consumir quantidade maior que a reservada")
    void shouldNotConsumeMoreThanReserved() {
        var stockItem = createStockItem();
        var command = ConsumeStockReservationCommand.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(RESERVED_QUANTITY + 1)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        var exception = assertThrows(ReservedStockExceededException.class,
                () -> this.consumeStockReservationAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), RESERVED_QUANTITY, RESERVED_QUANTITY + 1);
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve consumir reserva com quantidade zero")
    void shouldNotConsumeZeroQuantity() {
        var stockItem = createStockItem();
        var command = ConsumeStockReservationCommand.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(0)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        var exception = assertThrows(InvalidStockQuantityException.class,
                () -> this.consumeStockReservationAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), 0);
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve consumir reserva de estoque inexistente")
    void shouldNotConsumeWhenStockItemNotFound() {
        var command = ConsumeStockReservationCommand.builder()
                .suppliesId(SuppliesId.generate())
                .quantity(1)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId()))
                .thenThrow(new StockItemNotFoundException(command.suppliesId().toString()));

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.consumeStockReservationAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }
}
