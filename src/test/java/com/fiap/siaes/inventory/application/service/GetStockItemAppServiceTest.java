package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.inventory.InventoryTestFactory.QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.RESERVED_QUANTITY;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Stock Item")
class GetStockItemAppServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private SuppliesRepository suppliesRepository;

    @InjectMocks
    private GetStockItemAppService getStockItemAppService;

    @Test
    @DisplayName("Deve retornar o estoque com o insumo e as quantidades total, reservada e disponível")
    void shouldGetStockItem() {
        var stockItem = createStockItem();
        var supplies = createSupplies();

        when(this.stockItemRepository.findByIdOrThrowNotFound(stockItem.getSuppliesId())).thenReturn(stockItem);
        when(this.suppliesRepository.findByIdOrThrowNotFound(stockItem.getSuppliesId())).thenReturn(supplies);

        StockItemResponse response = this.getStockItemAppService.execute(stockItem.getSuppliesId());

        assertEquals(supplies.getId(), response.supplies().id());
        assertEquals(SKU, response.supplies().sku());
        assertEquals(DESCRIPTION, response.supplies().description());
        assertEquals(QUANTITY, response.quantity());
        assertEquals(RESERVED_QUANTITY, response.reservedQuantity());
        assertEquals(QUANTITY - RESERVED_QUANTITY, response.availableQuantity());
    }

    @Test
    @DisplayName("Não deve retornar estoque inexistente")
    void shouldNotGetNonExistentStockItem() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemRepository.findByIdOrThrowNotFound(suppliesId))
                .thenThrow(new StockItemNotFoundException(suppliesId.toString()));

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.getStockItemAppService.execute(suppliesId));

        assertExceptionParameters(exception, suppliesId.toString());
    }
}
