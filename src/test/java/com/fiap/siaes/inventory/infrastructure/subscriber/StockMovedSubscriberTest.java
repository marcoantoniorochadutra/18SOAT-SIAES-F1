package com.fiap.siaes.inventory.infrastructure.subscriber;

import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase;
import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase.RecordStockMovementCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovedEvent;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Stock Moved")
class StockMovedSubscriberTest {

    @Mock
    private RecordStockMovementUseCase recordStockMovementUseCase;

    @InjectMocks
    private StockMovedSubscriber stockMovedSubscriber;

    @Test
    @DisplayName("Deve gravar a movimentação de estoque recebida")
    void shouldRecordStockMovement() {
        var event = createStockMovedEvent();

        this.stockMovedSubscriber.on(event);

        verify(this.recordStockMovementUseCase).execute(RecordStockMovementCommand.of(event));
    }

    @Test
    @DisplayName("Deve propagar falha na gravação para desfazer a movimentação de estoque na mesma transação")
    void shouldPropagateFailure() {
        var event = createStockMovedEvent();

        doThrow(new IllegalStateException("database down"))
                .when(this.recordStockMovementUseCase).execute(RecordStockMovementCommand.of(event));

        assertThrows(IllegalStateException.class, () -> this.stockMovedSubscriber.on(event));
    }
}
