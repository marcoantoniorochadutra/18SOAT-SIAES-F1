package com.fiap.siaes.vehicle.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fiap.siaes.vehicle.infrastructure.persistence.mapper.VehicleMapper;
import com.fiap.siaes.vehicle.infrastructure.persistence.projection.ListVehicleProjection;
import com.fiap.siaes.vehicle.infrastructure.persistence.repository.VehicleJpaRepository;
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
import java.util.UUID;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicleJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Vehicle")
class VehicleRepositoryAdapterTest {

    @Mock
    private VehicleJpaRepository vehicleJpaRepository;

    @InjectMocks
    private VehicleRepositoryAdapter vehicleRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o veículo e retornar o agregado reconstruído")
    void shouldSaveVehicle() {
        var vehicle = createVehicle();

        when(this.vehicleJpaRepository.save(any(VehicleJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle savedVehicle = this.vehicleRepositoryAdapter.save(vehicle);

        VehicleJpaEntity savedEntity = captureSave(this.vehicleJpaRepository, VehicleJpaEntity.class);

        assertEquals(vehicle.getId(), savedEntity.getId());
        assertEquals(vehicle.getCustomerId(), savedEntity.getCustomerId());
        assertEquals(vehicle.getPlate().getValue(), savedEntity.getPlate());
        assertEquals(vehicle.getBrand(), savedEntity.getBrand());
        assertEquals(vehicle.getModel(), savedEntity.getModel());
        assertEquals(vehicle.getYear(), savedEntity.getYear());
        assertEquals(vehicle.getId(), savedVehicle.getId());
    }

    @Test
    @DisplayName("Deve buscar um veículo pelo id")
    void shouldFindVehicleById() {
        var entity = createVehicleJpaEntity();

        when(this.vehicleJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Vehicle vehicleFound = this.vehicleRepositoryAdapter.findById(entity.getId()).orElse(null);

        assertNotNull(vehicleFound);
        assertEquals(entity.getId(), vehicleFound.getId());
        assertEquals(entity.getCustomerId(), vehicleFound.getCustomerId());
        assertEquals(entity.getPlate(), vehicleFound.getPlate().getValue());
        assertEquals(entity.getBrand(), vehicleFound.getBrand());
        assertEquals(entity.getModel(), vehicleFound.getModel());
        assertEquals(entity.getYear(), vehicleFound.getYear());
    }

    @Test
    @DisplayName("Não deve encontrar veículo inexistente")
    void shouldNotFindNonExistentVehicle() {
        var id = VehicleId.generate();

        when(this.vehicleJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(this.vehicleRepositoryAdapter.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar um veículo pelo id e lançar erro quando não encontrado")
    void shouldFindVehicleByIdAndThrowErrorWhenNotFound() {
        var id = VehicleId.generate();

        when(this.vehicleJpaRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.vehicleRepositoryAdapter.findByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve verificar se o veículo existe e lançar erro quando não encontrado")
    void shouldCheckIfVehicleExistsAndThrowErrorWhenNotFound() {
        var id = VehicleId.generate();

        when(this.vehicleJpaRepository.existsById(id)).thenReturn(false);

        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.vehicleRepositoryAdapter.existsByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve verificar se já existe veículo com a placa informada")
    void shouldCheckIfPlateAlreadyExists() {
        when(this.vehicleJpaRepository.existsByPlate(PLATE)).thenReturn(true);

        assertThat(this.vehicleRepositoryAdapter.existsByPlate(Plate.create("abc-1d23"))).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe outro veículo com a placa informada")
    void shouldCheckIfPlateBelongsToAnotherVehicle() {
        var id = VehicleId.generate();

        when(this.vehicleJpaRepository.existsByPlateAndIdNot(PLATE, id)).thenReturn(true);

        assertThat(this.vehicleRepositoryAdapter.existsByPlateAndIdNot(Plate.create(PLATE), id)).isTrue();
    }

    @Test
    @DisplayName("Deve listar os veículos da página com os filtros informados")
    void shouldFindAllByFilter() {
        var customerId = CustomerId.generate();
        var filter = VehicleFilter.builder()
                .plate("abc-1d23")
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .customerId(customerId)
                .build();
        var pageQuery = PageQuery.builder().page(0).size(10).build();

        ListVehicleProjection projection = this.createListVehicleProjection(customerId);

        when(this.vehicleJpaRepository.findAllVehicles(
                PLATE, "Fiat", "Uno", 2020, customerId, PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(projection)));

        PageResponse<VehicleResponse> response = this.vehicleRepositoryAdapter.findAllByFilter(filter, pageQuery);

        assertThat(response.content()).containsExactly(VehicleMapper.toResponse(projection));
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Deve remover um veículo pelo id")
    void shouldDeleteVehicleById() {
        var id = VehicleId.generate();

        this.vehicleRepositoryAdapter.deleteById(id);

        verify(this.vehicleJpaRepository).deleteById(id);
    }

    private ListVehicleProjection createListVehicleProjection(CustomerId customerId) {
        var vehicleId = UUID.randomUUID();

        return new ListVehicleProjection() {
            @Override
            public UUID getId() {
                return vehicleId;
            }

            @Override
            public String getPlate() {
                return PLATE;
            }

            @Override
            public String getModel() {
                return "Uno";
            }

            @Override
            public int getYear() {
                return 2020;
            }

            @Override
            public UUID getCustomerId() {
                return customerId.id();
            }

            @Override
            public String getCustomerName() {
                return "Maria Silva";
            }
        };
    }
}
