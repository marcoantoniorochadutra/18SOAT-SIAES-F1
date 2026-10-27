package com.fiap.siaes.inventory.infrastructure.persistence.adapter;

import com.fiap.siaes.inventory.domain.exception.StockItemNotFoundException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockItemJpaEntity;
import com.fiap.siaes.inventory.infrastructure.persistence.repository.StockItemJpaRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItem;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockItemJpaEntity;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Stock Item")
class StockItemRepositoryAdapterTest {

    @Mock
    private StockItemJpaRepository stockItemJpaRepository;

    @InjectMocks
    private StockItemRepositoryAdapter stockItemRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o estoque e retornar o agregado reconstruído")
    void shouldSaveStockItem() {
        var stockItem = createStockItem();

        when(this.stockItemJpaRepository.save(any(StockItemJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockItem savedStockItem = this.stockItemRepositoryAdapter.save(stockItem);

        StockItemJpaEntity savedEntity = captureSave(this.stockItemJpaRepository, StockItemJpaEntity.class);

        assertEquals(stockItem.getSuppliesId(), savedEntity.getSuppliesId());
        assertEquals(stockItem.getQuantity(), savedEntity.getQuantity());
        assertEquals(stockItem.getReservedQuantity(), savedEntity.getReservedQuantity());
        assertEquals(stockItem.getVersion(), savedEntity.getVersion());
        assertEquals(stockItem.getSuppliesId(), savedStockItem.getSuppliesId());
        assertEquals(stockItem.getQuantity(), savedStockItem.getQuantity());
    }

    @Test
    @DisplayName("Deve buscar o estoque pelo id da peça/insumo")
    void shouldFindStockItemById() {
        var entity = createStockItemJpaEntity();

        when(this.stockItemJpaRepository.findById(entity.getSuppliesId())).thenReturn(Optional.of(entity));

        StockItem stockItemFound = this.stockItemRepositoryAdapter.findById(entity.getSuppliesId()).orElse(null);

        assertNotNull(stockItemFound);
        assertEquals(entity.getSuppliesId(), stockItemFound.getSuppliesId());
        assertEquals(entity.getQuantity(), stockItemFound.getQuantity());
        assertEquals(entity.getReservedQuantity(), stockItemFound.getReservedQuantity());
        assertEquals(entity.getVersion(), stockItemFound.getVersion());
    }

    @Test
    @DisplayName("Não deve encontrar estoque inexistente")
    void shouldNotFindNonExistentStockItem() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemJpaRepository.findById(suppliesId)).thenReturn(Optional.empty());

        assertThat(this.stockItemRepositoryAdapter.findById(suppliesId)).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar o estoque pelo id e retorná-lo quando encontrado")
    void shouldFindStockItemByIdOrThrowNotFound() {
        var entity = createStockItemJpaEntity();

        when(this.stockItemJpaRepository.findById(entity.getSuppliesId())).thenReturn(Optional.of(entity));

        StockItem stockItemFound = this.stockItemRepositoryAdapter.findByIdOrThrowNotFound(entity.getSuppliesId());

        assertEquals(entity.getSuppliesId(), stockItemFound.getSuppliesId());
    }

    @Test
    @DisplayName("Deve buscar o estoque pelo id e lançar erro quando não encontrado")
    void shouldFindStockItemByIdAndThrowErrorWhenNotFound() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemJpaRepository.findById(suppliesId)).thenReturn(Optional.empty());

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.stockItemRepositoryAdapter.findByIdOrThrowNotFound(suppliesId));

        assertExceptionParameters(exception, suppliesId.toString());
    }

    @Test
    @DisplayName("Deve verificar se existe estoque para a peça/insumo")
    void shouldCheckIfStockItemExists() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemJpaRepository.existsById(suppliesId)).thenReturn(true);

        assertThat(this.stockItemRepositoryAdapter.existsById(suppliesId)).isTrue();
    }

    @Test
    @DisplayName("Não deve lançar erro ao verificar a existência de estoque cadastrado")
    void shouldNotThrowWhenStockItemExists() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemJpaRepository.existsById(suppliesId)).thenReturn(true);

        assertDoesNotThrow(() -> this.stockItemRepositoryAdapter.existsByIdOrThrowNotFound(suppliesId));
    }

    @Test
    @DisplayName("Deve lançar erro ao verificar a existência de estoque não cadastrado")
    void shouldThrowWhenStockItemDoesNotExist() {
        var suppliesId = SuppliesId.generate();

        when(this.stockItemJpaRepository.existsById(suppliesId)).thenReturn(false);

        var exception = assertThrows(StockItemNotFoundException.class,
                () -> this.stockItemRepositoryAdapter.existsByIdOrThrowNotFound(suppliesId));

        assertExceptionParameters(exception, suppliesId.toString());
    }
}
