package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase.ListStockMovementsCommand;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovement;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Stock Movements")
class ListStockMovementsAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private ListStockMovementsAppService listStockMovementsAppService;

    @Test
    @DisplayName("Deve listar as movimentações do estoque da peça/insumo informada")
    void shouldListStockMovements() {
        var suppliesId = SuppliesId.generate();
        var pageQuery = PageQuery.of(0, 2);
        var movements = List.of(
                StockMovementResponse.from(createStockMovement()),
                StockMovementResponse.from(createStockMovement()));
        var page = PageResponse.from(new SliceImpl<>(movements));

        when(this.stockMovementRepository.findAllBySuppliesId(suppliesId, pageQuery)).thenReturn(page);

        PageResponse<StockMovementResponse> response = this.listStockMovementsAppService
                .execute(ListStockMovementsCommand.of(suppliesId, pageQuery));

        assertSame(page, response);
        assertEquals(movements, response.content());
        assertFalse(response.hasNext());
        verify(this.stockItemRepository).existsByIdOrThrowNotFound(suppliesId);
    }

    @Test
    @DisplayName("Não deve listar movimentações de estoque inexistente")
    void shouldNotListMovementsWhenStockItemNotFound() {
        var suppliesId = SuppliesId.generate();
        var command = ListStockMovementsCommand.of(suppliesId, PageQuery.of(null, null));

        doThrow(new StockItemNotFoundException(suppliesId.toString()))
                .when(this.stockItemRepository).existsByIdOrThrowNotFound(suppliesId);

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.listStockMovementsAppService.execute(command));

        assertExceptionParameters(exception, suppliesId.toString());
        verifyNoInteractions(this.stockMovementRepository);
    }
}
