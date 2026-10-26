package com.fiap.siaes.supplies.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.persistence.entity.SuppliesJpaEntity;
import com.fiap.siaes.supplies.infrastructure.persistence.mapper.SuppliesMapper;
import com.fiap.siaes.supplies.infrastructure.persistence.projection.ListSuppliesProjection;
import com.fiap.siaes.supplies.infrastructure.persistence.repository.SuppliesJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.UNIT_PRICE;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSuppliesJpaEntity;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Supplies")
class SuppliesRepositoryAdapterTest {

    @Mock
    private SuppliesJpaRepository suppliesJpaRepository;

    @InjectMocks
    private SuppliesRepositoryAdapter suppliesRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar a peça/insumo e retornar o agregado reconstruído")
    void shouldSaveSupplies() {
        var supplies = createSupplies();

        when(this.suppliesJpaRepository.save(any(SuppliesJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Supplies savedSupplies = this.suppliesRepositoryAdapter.save(supplies);

        SuppliesJpaEntity savedEntity = captureSave(this.suppliesJpaRepository, SuppliesJpaEntity.class);

        assertEquals(supplies.getId(), savedEntity.getId());
        assertEquals(supplies.getSku().getValue(), savedEntity.getSku());
        assertEquals(supplies.getDescription(), savedEntity.getDescription());
        assertEquals(supplies.getUnitPrice(), savedEntity.getUnitPrice());
        assertEquals(supplies.isActive(), savedEntity.isActive());
        assertEquals(supplies.getId(), savedSupplies.getId());
    }

    @Test
    @DisplayName("Deve buscar uma peça/insumo pelo id")
    void shouldFindSuppliesById() {
        var entity = createSuppliesJpaEntity();

        when(this.suppliesJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Supplies suppliesFound = this.suppliesRepositoryAdapter.findById(entity.getId()).orElse(null);

        assertNotNull(suppliesFound);
        assertEquals(entity.getId(), suppliesFound.getId());
        assertEquals(entity.getSku(), suppliesFound.getSku().getValue());
        assertEquals(entity.getDescription(), suppliesFound.getDescription());
        assertEquals(entity.getUnitPrice(), suppliesFound.getUnitPrice());
        assertEquals(entity.isActive(), suppliesFound.isActive());
    }

    @Test
    @DisplayName("Não deve encontrar peça/insumo inexistente")
    void shouldNotFindNonExistentSupplies() {
        var id = SuppliesId.generate();

        when(this.suppliesJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(this.suppliesRepositoryAdapter.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar uma peça/insumo pelo id e lançar erro quando não encontrada")
    void shouldFindSuppliesByIdAndThrowErrorWhenNotFound() {
        var id = SuppliesId.generate();

        when(this.suppliesJpaRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(SuppliesNotFoundException.class,
                () -> this.suppliesRepositoryAdapter.findByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve verificar se já existe peça/insumo com o SKU informado")
    void shouldCheckIfSkuExists() {
        when(this.suppliesJpaRepository.existsBySku(SKU)).thenReturn(true);

        assertThat(this.suppliesRepositoryAdapter.existsBySku(Sku.create("flt-oleo-001"))).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe outra peça/insumo com o SKU informado")
    void shouldCheckIfSkuBelongsToAnotherSupplies() {
        var id = SuppliesId.generate();

        when(this.suppliesJpaRepository.existsBySkuAndIdNot(SKU, id)).thenReturn(true);

        assertThat(this.suppliesRepositoryAdapter.existsBySkuAndIdNot(Sku.create("flt-oleo-001"), id)).isTrue();
    }

    @Test
    @DisplayName("Deve listar as peças/insumos da página com os filtros informados")
    void shouldFindAllByFilter() {
        var filter = SuppliesFilter.of("flt-oleo-001", DESCRIPTION, null);
        var pageQuery = PageQuery.of(0, 10);

        ListSuppliesProjection projection = this.createListSuppliesProjection();

        when(this.suppliesJpaRepository.findAllSupplies(SKU, DESCRIPTION, true, PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(projection)));

        PageResponse<SuppliesResponse> response = this.suppliesRepositoryAdapter.findAllByFilter(filter, pageQuery);

        assertThat(response.content()).containsExactly(SuppliesMapper.toResponse(projection));
        assertThat(response.hasNext()).isFalse();
    }

    private ListSuppliesProjection createListSuppliesProjection() {
        var suppliesId = UUID.randomUUID();

        return new ListSuppliesProjection() {
            @Override
            public UUID getId() {
                return suppliesId;
            }

            @Override
            public String getSku() {
                return SKU;
            }

            @Override
            public String getDescription() {
                return DESCRIPTION;
            }

            @Override
            public BigDecimal getUnitPrice() {
                return UNIT_PRICE;
            }

            @Override
            public boolean isActive() {
                return true;
            }
        };
    }
}
