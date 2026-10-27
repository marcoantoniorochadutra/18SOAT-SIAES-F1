package com.fiap.siaes.quotation.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.mapper.QuotationMapper;
import com.fiap.siaes.quotation.infrastructure.persistence.projection.ListQuotationProjection;
import com.fiap.siaes.quotation.infrastructure.persistence.repository.QuotationJpaRepository;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Quotation")
class QuotationRepositoryAdapterTest {

    @Mock
    private QuotationJpaRepository quotationJpaRepository;

    @InjectMocks
    private QuotationRepositoryAdapter quotationRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o orçamento e retornar o agregado reconstruído")
    void shouldSaveQuotation() {
        Quotation quotation = createQuotation();

        when(this.quotationJpaRepository.save(any(QuotationJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Quotation saved = this.quotationRepositoryAdapter.save(quotation);

        QuotationJpaEntity savedEntity = captureSave(this.quotationJpaRepository, QuotationJpaEntity.class);

        assertEquals(quotation.getId(), savedEntity.getId());
        assertEquals(quotation.getWorkOrderId(), savedEntity.getWorkOrderId());
        assertEquals(quotation.getId(), saved.getId());
        assertEquals(quotation.getTotalAmount(), saved.getTotalAmount());
    }

    @Test
    @DisplayName("Deve buscar o orçamento pelo id")
    void shouldFindQuotationById() {
        QuotationJpaEntity entity = QuotationMapper.toEntity(createQuotation());

        when(this.quotationJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Quotation found = this.quotationRepositoryAdapter.findByIdOrThrowNotFound(entity.getId());

        assertEquals(entity.getId(), found.getId());
        assertEquals(entity.getWorkOrderId(), found.getWorkOrderId());
    }

    @Test
    @DisplayName("Deve lançar erro quando o orçamento não for encontrado")
    void shouldThrowNotFoundWhenQuotationDoesNotExist() {
        var id = QuotationId.generate();

        when(this.quotationJpaRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(QuotationNotFoundException.class,
                                     () -> this.quotationRepositoryAdapter.findByIdOrThrowNotFound(id));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve verificar se a ordem de serviço já possui orçamento em aberto")
    void shouldCheckIfWorkOrderHasOpenQuotation() {
        var workOrderId = WorkOrderId.generate();

        when(this.quotationJpaRepository.existsByWorkOrderIdAndLastStatusNotIn(workOrderId, List.of(QuotationStatus.REJECTED, QuotationStatus.DISCARDED)))
                .thenReturn(true);

        assertThat(this.quotationRepositoryAdapter.existsOpenByWorkOrderId(workOrderId)).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se a ordem de serviço possui orçamento aguardando aprovação")
    void shouldCheckIfWorkOrderHasQuotationAwaitingApproval() {
        var workOrderId = WorkOrderId.generate();

        when(this.quotationJpaRepository.existsByWorkOrderIdAndLastStatus(workOrderId, QuotationStatus.AWAITING_APPROVAL))
                .thenReturn(true);

        assertThat(this.quotationRepositoryAdapter.existsAwaitingApprovalByWorkOrderId(workOrderId)).isTrue();
    }

    @Test
    @DisplayName("Deve buscar o orçamento em rascunho da ordem de serviço")
    void shouldFindDraftByWorkOrderId() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);

        when(this.quotationJpaRepository.findByWorkOrderIdAndLastStatus(quotation.getWorkOrderId(), QuotationStatus.DRAFT))
                .thenReturn(Optional.of(QuotationMapper.toEntity(quotation)));

        assertThat(this.quotationRepositoryAdapter.findDraftByWorkOrderId(quotation.getWorkOrderId()))
                .hasValueSatisfying(found -> assertEquals(quotation.getId(), found.getId()));
    }

    @Test
    @DisplayName("Deve listar os orçamentos da página com os filtros informados")
    void shouldFindAllByFilter() {
        var filter = QuotationFilter.of(WorkOrderId.generate(), CustomerId.generate(), VehicleId.generate(),
                                        QuotationStatus.AWAITING_APPROVAL);
        ListQuotationProjection projection = this.createListQuotationProjection();

        when(this.quotationJpaRepository.findAllQuotations(filter.workOrderId(), filter.customerId(), filter.vehicleId(),
                                                           "AWAITING_APPROVAL", true, PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(projection)));

        PageResponse<ListQuotationResponse> response = this.quotationRepositoryAdapter.findAllByFilter(filter, true, PageQuery.of(0, 10));

        ListQuotationResponse quotation = response.content().getFirst();
        assertEquals(projection.getId(), quotation.id().id());
        assertEquals(projection.getWorkOrderId(), quotation.workOrderId().id());
        assertEquals(projection.getCustomerId(), quotation.customer().id().id());
        assertEquals(projection.getCustomerName(), quotation.customer().name());
        assertEquals(projection.getVehicleId(), quotation.vehicle().id().id());
        assertEquals(projection.getVehiclePlate(), quotation.vehicle().plate());
        assertEquals(projection.getVehicleBrand(), quotation.vehicle().brand());
        assertEquals(projection.getVehicleModel(), quotation.vehicle().model());
        assertEquals(DESCRIPTION, quotation.description());
        assertEquals(QuotationStatus.AWAITING_APPROVAL, quotation.status());
        assertEquals(projection.getTotalAmount(), quotation.totalAmount());
        assertEquals(projection.getCreatedAt(), quotation.createdAt());
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Deve listar os orçamentos sem filtro de status")
    void shouldFindAllWithoutStatusFilter() {
        var filter = QuotationFilter.of(null, null, null, null);

        when(this.quotationJpaRepository.findAllQuotations(null, null, null, null, false, PageRequest.of(0, 20)))
                .thenReturn(new SliceImpl<>(List.of()));

        PageResponse<ListQuotationResponse> response = this.quotationRepositoryAdapter.findAllByFilter(filter, false, PageQuery.of(0, 20));

        assertThat(response.content()).isEmpty();
    }

    private ListQuotationProjection createListQuotationProjection() {
        var id = UUID.randomUUID();
        var workOrderId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();
        var createdAt = Instant.now();

        return new ListQuotationProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public UUID getWorkOrderId() {
                return workOrderId;
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
            public String getDescription() {
                return DESCRIPTION;
            }

            @Override
            public String getStatus() {
                return "AWAITING_APPROVAL";
            }

            @Override
            public BigDecimal getTotalAmount() {
                return new BigDecimal("125.90");
            }

            @Override
            public Instant getCreatedAt() {
                return createdAt;
            }
        };
    }
}
