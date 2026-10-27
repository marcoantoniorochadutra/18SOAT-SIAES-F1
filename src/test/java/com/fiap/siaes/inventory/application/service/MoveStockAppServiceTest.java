package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase.MoveStockCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.exception.InvalidStockMovementTypeException;
import com.fiap.siaes.inventory.domain.exception.StockAdjustmentReasonRequiredException;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.model.enums.StockMovementType;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.UserTestFactory;
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
import static com.fiap.siaes.inventory.InventoryTestFactory.REASON;
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
@DisplayName("UnitTest - App Service - Move Stock")
class MoveStockAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private MoveStockAppService moveStockAppService;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Test
    @DisplayName("Deve registrar a entrada de estoque e publicar a movimentação com o usuário autenticado")
    void shouldMoveStockEntrySuccessfully() {
        var stockItem = createStockItem();
        var authenticatedUser = UserTestFactory.authenticatedUserManager();
        var command = this.commandBuilder(stockItem.getSuppliesId())
                .authenticatedUser(authenticatedUser)
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        SuppliesId suppliesId = this.moveStockAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(stockItem.getSuppliesId(), suppliesId);
        assertEquals(QUANTITY + 5, savedStockItem.getQuantity());
        assertEquals(RESERVED_QUANTITY, savedStockItem.getReservedQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(StockMovementType.ENTRY, event.type());
            assertEquals(5, event.quantity());
            assertEquals(QUANTITY + 5, event.stockAfter());
            assertEquals(authenticatedUser.id(), event.performedBy());
        });
    }

    @Test
    @DisplayName("Deve registrar o ajuste negativo de estoque com o motivo informado")
    void shouldMoveStockAdjustmentSuccessfully() {
        var stockItem = createStockItem();
        var command = this.commandBuilder(stockItem.getSuppliesId())
                .type(StockMovementType.ADJUSTMENT)
                .quantity(-2)
                .reason(REASON)
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        this.moveStockAppService.execute(command);

        StockItem savedStockItem = captureSave(this.stockItemRepository, StockItem.class);

        assertEquals(QUANTITY - 2, savedStockItem.getQuantity());

        verify(this.domainEventPublisher).publish(this.eventsCaptor.capture());

        assertThat(this.eventsCaptor.getValue()).singleElement().isInstanceOfSatisfying(StockMovedEvent.class, event -> {
            assertEquals(StockMovementType.ADJUSTMENT, event.type());
            assertEquals(-2, event.quantity());
            assertEquals(REASON, event.reason());
        });
    }

    @Test
    @DisplayName("Não deve registrar ajuste de estoque sem motivo")
    void shouldNotMoveStockAdjustmentWithoutReason() {
        var stockItem = createStockItem();
        var command = this.commandBuilder(stockItem.getSuppliesId())
                .type(StockMovementType.ADJUSTMENT)
                .reason(null)
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        var exception = assertThrows(StockAdjustmentReasonRequiredException.class,
                () -> this.moveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve registrar movimentação manual com tipo reservado ao sistema")
    void shouldNotMoveStockWithSystemType() {
        var stockItem = createStockItem();
        var command = this.commandBuilder(stockItem.getSuppliesId())
                .type(StockMovementType.CONSUMPTION)
                .build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId())).thenReturn(stockItem);

        var exception = assertThrows(InvalidStockMovementTypeException.class,
                () -> this.moveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString(), StockMovementType.CONSUMPTION.name());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve movimentar estoque inexistente")
    void shouldNotMoveStockWhenStockItemNotFound() {
        var command = this.commandBuilder(SuppliesId.generate()).build();

        when(this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId()))
                .thenThrow(new StockItemNotFoundException(command.suppliesId().toString()));

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.moveStockAppService.execute(command));

        assertExceptionParameters(exception, command.suppliesId().toString());
        verify(this.stockItemRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    private MoveStockCommand.MoveStockCommandBuilder commandBuilder(SuppliesId suppliesId) {
        return MoveStockCommand.builder()
                .suppliesId(suppliesId)
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .type(StockMovementType.ENTRY)
                .quantity(5);
    }
}
