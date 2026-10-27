package com.fiap.siaes.inventory.infrastructure.subscriber;

import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase;
import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase.CreateStockItemCommand;
import com.fiap.siaes.inventory.domain.exception.InvalidStockQuantityException;
import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Supplies Created")
class SuppliesCreatedSubscriberTest {

    @Mock
    private CreateStockItemUseCase createStockItemUseCase;

    @InjectMocks
    private SuppliesCreatedSubscriber suppliesCreatedSubscriber;

    @Test
    @DisplayName("Deve criar o estoque da peça/insumo criada com o estoque inicial e o usuário criador")
    void shouldCreateStockItemForCreatedSupplies() {
        var event = SuppliesCreatedEvent.of(SuppliesId.generate(), QUANTITY, UserId.generate(), Instant.now());

        this.suppliesCreatedSubscriber.on(event);

        verify(this.createStockItemUseCase)
                .execute(CreateStockItemCommand.of(event.suppliesId(), QUANTITY, event.createdBy()));
    }

    @Test
    @DisplayName("Deve propagar falha na criação do estoque para desfazer a criação da peça/insumo")
    void shouldPropagateFailure() {
        var event = SuppliesCreatedEvent.of(SuppliesId.generate(), -1, UserId.generate(), Instant.now());
        var command = CreateStockItemCommand.of(event.suppliesId(), -1, event.createdBy());

        doThrow(new InvalidStockQuantityException(event.suppliesId().toString(), -1))
                .when(this.createStockItemUseCase).execute(command);

        assertThrows(InvalidStockQuantityException.class, () -> this.suppliesCreatedSubscriber.on(event));
    }
}
