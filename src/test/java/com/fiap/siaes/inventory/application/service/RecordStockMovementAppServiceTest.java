package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase.RecordStockMovementCommand;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovedEvent;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Record Stock Movement")
class RecordStockMovementAppServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private RecordStockMovementAppService recordStockMovementAppService;

    @Test
    @DisplayName("Deve gravar a movimentação de estoque a partir do evento recebido")
    void shouldRecordStockMovementFromEvent() {
        var event = createStockMovedEvent();

        this.recordStockMovementAppService.execute(RecordStockMovementCommand.of(event));

        StockMovement savedMovement = captureSave(this.stockMovementRepository, StockMovement.class);

        assertNotNull(savedMovement.getId());
        assertEquals(event.suppliesId(), savedMovement.getSuppliesId());
        assertEquals(event.type(), savedMovement.getType());
        assertEquals(event.quantity(), savedMovement.getQuantity());
        assertEquals(event.stockAfter(), savedMovement.getStockAfter());
        assertEquals(event.reservedAfter(), savedMovement.getReservedAfter());
        assertEquals(event.reason(), savedMovement.getReason());
        assertEquals(event.referenceId(), savedMovement.getReferenceId());
        assertEquals(event.performedBy(), savedMovement.getPerformedBy());
        assertEquals(event.occurredAt(), savedMovement.getOccurredAt());
    }
}
