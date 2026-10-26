package com.fiap.siaes.maintenance.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import com.fiap.siaes.maintenance.infrastructure.persistence.mapper.MaintenanceMapper;
import com.fiap.siaes.maintenance.infrastructure.persistence.projection.ListMaintenanceProjection;
import com.fiap.siaes.maintenance.infrastructure.persistence.repository.MaintenanceJpaRepository;
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

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.DESCRIPTION;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.ESTIMATED_DURATION_MINUTES;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.CODE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.PRICE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenanceJpaEntity;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Maintenance")
class MaintenanceRepositoryAdapterTest {

    @Mock
    private MaintenanceJpaRepository maintenanceJpaRepository;

    @InjectMocks
    private MaintenanceRepositoryAdapter maintenanceRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o serviço e retornar o agregado reconstruído")
    void shouldSaveMaintenance() {
        var maintenance = createMaintenance();

        when(this.maintenanceJpaRepository.save(any(MaintenanceJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Maintenance savedMaintenance = this.maintenanceRepositoryAdapter.save(maintenance);

        MaintenanceJpaEntity savedEntity = captureSave(this.maintenanceJpaRepository, MaintenanceJpaEntity.class);

        assertEquals(maintenance.getId(), savedEntity.getId());
        assertEquals(maintenance.getCode().getValue(), savedEntity.getCode());
        assertEquals(maintenance.getDescription(), savedEntity.getDescription());
        assertEquals(maintenance.getPrice(), savedEntity.getPrice());
        assertEquals(maintenance.getEstimatedDurationMinutes(), savedEntity.getEstimatedDurationMinutes());
        assertEquals(maintenance.isActive(), savedEntity.isActive());
        assertEquals(maintenance.getId(), savedMaintenance.getId());
    }

    @Test
    @DisplayName("Deve buscar um serviço pelo id")
    void shouldFindMaintenanceById() {
        var entity = createMaintenanceJpaEntity();

        when(this.maintenanceJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Maintenance maintenanceFound = this.maintenanceRepositoryAdapter.findById(entity.getId()).orElse(null);

        assertNotNull(maintenanceFound);
        assertEquals(entity.getId(), maintenanceFound.getId());
        assertEquals(entity.getCode(), maintenanceFound.getCode().getValue());
        assertEquals(entity.getDescription(), maintenanceFound.getDescription());
        assertEquals(entity.getPrice(), maintenanceFound.getPrice());
        assertEquals(entity.isActive(), maintenanceFound.isActive());
    }

    @Test
    @DisplayName("Não deve encontrar serviço inexistente")
    void shouldNotFindNonExistentMaintenance() {
        var id = MaintenanceId.generate();

        when(this.maintenanceJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(this.maintenanceRepositoryAdapter.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar um serviço pelo id e lançar erro quando não encontrado")
    void shouldFindMaintenanceByIdAndThrowErrorWhenNotFound() {
        var id = MaintenanceId.generate();

        when(this.maintenanceJpaRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(MaintenanceNotFoundException.class,
                () -> this.maintenanceRepositoryAdapter.findByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve verificar se já existe serviço com o código informado")
    void shouldCheckIfCodeExists() {
        when(this.maintenanceJpaRepository.existsByCode(CODE)).thenReturn(true);

        assertThat(this.maintenanceRepositoryAdapter.existsByCode(MaintenanceCode.create("troca-oleo"))).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe outro serviço com o código informado")
    void shouldCheckIfCodeBelongsToAnotherMaintenance() {
        var id = MaintenanceId.generate();

        when(this.maintenanceJpaRepository.existsByCodeAndIdNot(CODE, id)).thenReturn(true);

        assertThat(this.maintenanceRepositoryAdapter.existsByCodeAndIdNot(MaintenanceCode.create("troca-oleo"), id)).isTrue();
    }

    @Test
    @DisplayName("Deve listar os serviços da página com os filtros informados")
    void shouldFindAllByFilter() {
        var filter = MaintenanceFilter.of("troca-oleo", DESCRIPTION, null);
        var pageQuery = PageQuery.of(0, 10);

        ListMaintenanceProjection projection = this.createListMaintenanceProjection();

        when(this.maintenanceJpaRepository.findAllMaintenances(CODE, DESCRIPTION, true, PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(projection)));

        PageResponse<MaintenanceResponse> response = this.maintenanceRepositoryAdapter.findAllByFilter(filter, pageQuery);

        assertThat(response.content()).containsExactly(MaintenanceMapper.toResponse(projection));
        assertThat(response.hasNext()).isFalse();
    }

    private ListMaintenanceProjection createListMaintenanceProjection() {
        var maintenanceId = UUID.randomUUID();

        return new ListMaintenanceProjection() {
            @Override
            public UUID getId() {
                return maintenanceId;
            }

            @Override
            public String getCode() {
                return CODE;
            }

            @Override
            public String getDescription() {
                return DESCRIPTION;
            }

            @Override
            public BigDecimal getPrice() {
                return PRICE;
            }

            @Override
            public int getEstimatedDurationMinutes() {
                return ESTIMATED_DURATION_MINUTES;
            }

            @Override
            public boolean isActive() {
                return true;
            }
        };
    }
}
