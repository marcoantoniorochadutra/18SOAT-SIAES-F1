package com.fiap.siaes.workorder.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.mapper.WorkOrderMapper;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.ListWorkOrderProjection;
import com.fiap.siaes.workorder.infrastructure.persistence.projection.WorkOrderStatusHistoryProjection;
import com.fiap.siaes.workorder.infrastructure.persistence.repository.WorkOrderJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Work Order")
class WorkOrderRepositoryAdapterTest {

    @Mock
    private WorkOrderJpaRepository workOrderJpaRepository;

    @InjectMocks
    private WorkOrderRepositoryAdapter workOrderRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar a ordem de serviço e retornar o agregado reconstruído")
    void shouldSaveWorkOrder() {
        WorkOrder workOrder = createWorkOrder();

        when(this.workOrderJpaRepository.save(any(WorkOrderJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder saved = this.workOrderRepositoryAdapter.save(workOrder);

        WorkOrderJpaEntity savedEntity = captureSave(this.workOrderJpaRepository, WorkOrderJpaEntity.class);

        assertEquals(workOrder.getId(), savedEntity.getId());
        assertEquals(workOrder.getCustomerId(), savedEntity.getCustomerId());
        assertEquals(workOrder.getId(), saved.getId());
        assertEquals(workOrder.getLastStatus(), saved.getLastStatus());
    }

    @Test
    @DisplayName("Deve buscar a ordem de serviço pelo id")
    void shouldFindWorkOrderById() {
        WorkOrderJpaEntity entity = WorkOrderMapper.toEntity(createWorkOrder());

        when(this.workOrderJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        WorkOrder found = this.workOrderRepositoryAdapter.findByIdOrThrowNotFound(entity.getId());

        assertEquals(entity.getId(), found.getId());
        assertEquals(entity.getCustomerId(), found.getCustomerId());
    }

    @Test
    @DisplayName("Deve lançar erro quando a ordem de serviço não for encontrada")
    void shouldThrowNotFoundWhenWorkOrderDoesNotExist() {
        var id = WorkOrderId.generate();

        when(this.workOrderJpaRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(WorkOrderNotFoundException.class,
                                     () -> this.workOrderRepositoryAdapter.findByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve validar a existência da ordem de serviço")
    void shouldCheckWorkOrderExists() {
        var id = WorkOrderId.generate();

        when(this.workOrderJpaRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> this.workOrderRepositoryAdapter.existsByIdOrThrowNotFound(id));
    }

    @Test
    @DisplayName("Deve lançar erro quando a ordem de serviço validada não existir")
    void shouldThrowNotFoundWhenCheckedWorkOrderDoesNotExist() {
        var id = WorkOrderId.generate();

        when(this.workOrderJpaRepository.existsById(id)).thenReturn(false);

        var exception = assertThrows(WorkOrderNotFoundException.class,
                                     () -> this.workOrderRepositoryAdapter.existsByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve listar as ordens de serviço da página com os filtros informados")
    void shouldFindAllByFilter() {
        var filter = WorkOrderFilter.of(CustomerId.generate(), "529.982.247-25", VehicleId.generate(), WorkOrderStatus.IN_EXECUTION);
        ListWorkOrderProjection projection = this.createListWorkOrderProjection();

        when(this.workOrderJpaRepository.findAllWorkOrders(filter.customerId(), "52998224725", filter.vehicleId(), "IN_EXECUTION",
                                                           PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(projection)));

        PageResponse<ListWorkOrderResponse> response = this.workOrderRepositoryAdapter.findAllByFilter(filter, PageQuery.of(0, 10));

        ListWorkOrderResponse workOrder = response.content().getFirst();
        assertEquals(projection.getId(), workOrder.id().id());
        assertEquals(projection.getCustomerId(), workOrder.customer().id().id());
        assertEquals(projection.getCustomerName(), workOrder.customer().name());
        assertEquals(projection.getVehicleId(), workOrder.vehicle().id().id());
        assertEquals(projection.getVehiclePlate(), workOrder.vehicle().plate());
        assertEquals(projection.getVehicleBrand(), workOrder.vehicle().brand());
        assertEquals(projection.getVehicleModel(), workOrder.vehicle().model());
        assertEquals(WorkOrderStatus.IN_EXECUTION, workOrder.status());
        assertEquals(projection.getCreatedAt(), workOrder.createdAt());
        assertEquals(projection.getFinishedAt(), workOrder.finishedAt());
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Deve listar as ordens de serviço sem filtro de status")
    void shouldFindAllWithoutStatusFilter() {
        var filter = WorkOrderFilter.of(null, " ", null, null);

        when(this.workOrderJpaRepository.findAllWorkOrders(null, null, null, null, PageRequest.of(0, 20)))
                .thenReturn(new SliceImpl<>(List.of()));

        PageResponse<ListWorkOrderResponse> response = this.workOrderRepositoryAdapter.findAllByFilter(filter, PageQuery.of(0, 20));

        assertThat(response.content()).isEmpty();
    }

    @Test
    @DisplayName("Deve agrupar o histórico de status das ordens de serviço finalizadas no período")
    void shouldFindStatusTimelinesOfFinishedBetween() {
        var from = Instant.parse("2026-10-01T00:00:00Z");
        var to = Instant.parse("2026-10-31T23:59:59Z");
        var firstWorkOrderId = UUID.randomUUID();
        var secondWorkOrderId = UUID.randomUUID();
        var receivedAt = Instant.parse("2026-10-10T08:00:00Z");

        when(this.workOrderJpaRepository.findStatusHistoryOfFinishedBetween(from, to))
                .thenReturn(List.of(this.createStatusHistoryProjection(firstWorkOrderId, "RECEIVED", receivedAt),
                                    this.createStatusHistoryProjection(secondWorkOrderId, "IN_EXECUTION", receivedAt),
                                    this.createStatusHistoryProjection(firstWorkOrderId, "IN_DIAGNOSIS", receivedAt.plus(Duration.ofMinutes(30))),
                                    this.createStatusHistoryProjection(secondWorkOrderId, "FINISHED", receivedAt.plus(Duration.ofMinutes(90)))));

        List<StatusTimeline> timelines = this.workOrderRepositoryAdapter.findStatusTimelinesOfFinishedBetween(from, to);

        assertEquals(2, timelines.size());
        assertEquals(Duration.ofMinutes(30), timelines.getFirst().timeInStatus().get(WorkOrderStatus.RECEIVED));
        assertEquals(Duration.ofMinutes(90), timelines.getLast().timeInStatus().get(WorkOrderStatus.IN_EXECUTION));
    }

    private WorkOrderStatusHistoryProjection createStatusHistoryProjection(UUID workOrderId, String status, Instant updatedAt) {
        var id = UUID.randomUUID();

        return new WorkOrderStatusHistoryProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public UUID getWorkOrderId() {
                return workOrderId;
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public Instant getUpdatedAt() {
                return updatedAt;
            }
        };
    }

    private ListWorkOrderProjection createListWorkOrderProjection() {
        var id = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();
        var createdAt = Instant.now();

        return new ListWorkOrderProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public UUID getCustomerId() {
                return customerId;
            }

            @Override
            public String getCustomerName() {
                return "Maria Silva";
            }

            @Override
            public UUID getVehicleId() {
                return vehicleId;
            }

            @Override
            public String getVehiclePlate() {
                return "ABC1D23";
            }

            @Override
            public String getVehicleBrand() {
                return "Fiat";
            }

            @Override
            public String getVehicleModel() {
                return "Uno";
            }

            @Override
            public String getStatus() {
                return "IN_EXECUTION";
            }

            @Override
            public Instant getCreatedAt() {
                return createdAt;
            }

            @Override
            public Instant getFinishedAt() {
                return null;
            }
        };
    }
}
