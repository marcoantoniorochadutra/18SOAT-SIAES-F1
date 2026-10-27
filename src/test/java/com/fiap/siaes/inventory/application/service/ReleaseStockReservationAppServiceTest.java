package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase.ReleaseStockReservationCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
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
@DisplayName("UnitTest - App Service - Release Stock Reservation")
class ReleaseStockReservationAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReleaseStockReservationAppService releaseStockReservationAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve liberar a reserva de estoque e publicar a movimentação")
    void shouldReleaseReservationSuccessfully() {
        var stockItem = createStockItem();
        var command = ReleaseStockReservationCommand.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(RESERVED_QUANTITY)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        this.releaseStockReservationAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(QUANTITY, savedStockItem.getQuantity());
        assertEquals(0, savedStockItem.getReservedQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(StockMovementType.RESERVATION_RELEASE, event.type());
            assertEquals(RESERVED_QUANTITY, event.quantity());
            assertEquals(command.referenceId(), event.referenceId());
            assertEquals(command.performedBy(), event.performedBy());
        });
    }

    @Test
    @DisplayName("Não deve liberar quantidade maior que a reservada")
    void shouldNotReleaseMoreThanReserved() {
        var stockItem = createStockItem();
        var command = ReleaseStockReservationCommand.builder()
                .suppliesId(stockItem.getSuppliesId())
                .quantity(RESERVED_QUANTITY + 1)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        var exception = assertThrows(ReservedStockExceededException.class,
                () -> this.releaseStockReservationAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), RESERVED_QUANTITY, RESERVED_QUANTITY + 1);
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve liberar reserva de estoque inexistente")
    void shouldNotReleaseWhenStockItemNotFound() {
        var command = ReleaseStockReservationCommand.builder()
                .suppliesId(SuppliesId.generate())
                .quantity(1)
                .referenceId(UUID.randomUUID())
                .performedBy(UserId.generate())
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId()))
                .thenThrow(new StockItemNotFoundException(command.suppliesId().toString()));

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.releaseStockReservationAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }
}
