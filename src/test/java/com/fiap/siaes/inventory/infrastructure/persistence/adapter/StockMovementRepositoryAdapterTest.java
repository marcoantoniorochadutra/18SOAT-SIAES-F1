package com.fiap.siaes.inventory.infrastructure.persistence.adapter;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.fiap.siaes.inventory.infrastructure.persistence.repository.StockMovementJpaRepository;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovement;
import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovementJpaEntity;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Stock Movement")
class StockMovementRepositoryAdapterTest {

    @Mock
    private StockMovementJpaRepository stockMovementJpaRepository;

    @InjectMocks
    private StockMovementRepositoryAdapter stockMovementRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar a movimentação de estoque e retornar o domínio reconstruído")
    void shouldSaveStockMovement() {
        var stockMovement = createStockMovement();

        when(this.stockMovementJpaRepository.save(any(StockMovementJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockMovement savedMovement = this.stockMovementRepositoryAdapter.save(stockMovement);

        StockMovementJpaEntity savedEntity = captureSave(this.stockMovementJpaRepository, StockMovementJpaEntity.class);

        assertEquals(stockMovement.getId(), savedEntity.getId());
        assertEquals(stockMovement.getSuppliesId(), savedEntity.getSuppliesId());
        assertEquals(stockMovement.getType(), savedEntity.getType());
        assertEquals(stockMovement.getQuantity(), savedEntity.getQuantity());
        assertEquals(stockMovement.getOccurredAt(), savedEntity.getOccurredAt());
        assertEquals(stockMovement.getId(), savedMovement.getId());
    }

    @Test
    @DisplayName("Deve buscar uma movimentação de estoque pelo id")
    void shouldFindStockMovementById() {
        var entity = createStockMovementJpaEntity();

        when(this.stockMovementJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        StockMovement movementFound = this.stockMovementRepositoryAdapter.findById(entity.getId()).orElse(null);

        assertNotNull(movementFound);
        assertEquals(entity.getId(), movementFound.getId());
        assertEquals(entity.getSuppliesId(), movementFound.getSuppliesId());
        assertEquals(entity.getType(), movementFound.getType());
    }

    @Test
    @DisplayName("Não deve encontrar movimentação de estoque inexistente")
    void shouldNotFindNonExistentStockMovement() {
        var id = StockMovementId.generate();

        when(this.stockMovementJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(this.stockMovementRepositoryAdapter.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve listar as movimentações da peça/insumo da página informada")
    void shouldFindAllBySuppliesId() {
        var suppliesId = SuppliesId.generate();
        var entity = createStockMovementJpaEntity();
        var pageRequest = PageRequest.of(1, 5);

        when(this.stockMovementJpaRepository.findAllBySuppliesIdOrderByOccurredAtDesc(suppliesId, pageRequest))
                .thenReturn(new SliceImpl<>(List.of(entity), pageRequest, true));

        PageResponse<StockMovementResponse> response = this.stockMovementRepositoryAdapter
                .findAllBySuppliesId(suppliesId, PageQuery.of(1, 5));

        assertThat(response.content()).singleElement().satisfies(movement -> {
            assertEquals(entity.getId(), movement.id());
            assertEquals(entity.getType(), movement.type());
            assertEquals(entity.getQuantity(), movement.quantity());
            assertEquals(entity.getStockAfter(), movement.stockAfter());
            assertEquals(entity.getReservedAfter(), movement.reservedAfter());
            assertEquals(entity.getReason(), movement.reason());
            assertEquals(entity.getReferenceId(), movement.referenceId());
            assertEquals(entity.getPerformedBy(), movement.performedBy());
            assertEquals(entity.getOccurredAt(), movement.occurredAt());
        });
        assertThat(response.hasNext()).isTrue();
        assertEquals(1, response.page());
        assertEquals(5, response.size());
    }
}
