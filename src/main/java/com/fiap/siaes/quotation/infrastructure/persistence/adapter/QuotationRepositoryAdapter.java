package com.fiap.siaes.quotation.infrastructure.persistence.adapter;

import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.port.QuotationQueryPort;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationJpaEntity;
import com.fiap.siaes.quotation.infrastructure.persistence.mapper.QuotationMapper;
import com.fiap.siaes.quotation.infrastructure.persistence.repository.QuotationJpaRepository;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

@Repository
@RequiredArgsConstructor
public class QuotationRepositoryAdapter implements QuotationRepository, QuotationQueryPort {

    private final QuotationJpaRepository quotationJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Quotation save(Quotation quotation) {
        QuotationJpaEntity entity = QuotationMapper.toEntity(quotation);

        QuotationJpaEntity saved = this.quotationJpaRepository.save(entity);

        return QuotationMapper.toDomain(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<Quotation> findById(QuotationId id) {
        return this.quotationJpaRepository.findById(id)
                .map(QuotationMapper::toDomain);
    }

    @Override
    public boolean existsOpenByWorkOrderId(WorkOrderId workOrderId) {
        return this.quotationJpaRepository.existsByWorkOrderIdAndLastStatusNotIn(workOrderId, List.of(QuotationStatus.REJECTED, QuotationStatus.DISCARDED));
    }

    @Override
    public boolean existsAwaitingApprovalByWorkOrderId(WorkOrderId workOrderId) {
        return this.quotationJpaRepository.existsByWorkOrderIdAndLastStatus(workOrderId, QuotationStatus.AWAITING_APPROVAL);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<Quotation> findDraftByWorkOrderId(WorkOrderId workOrderId) {
        return this.quotationJpaRepository.findByWorkOrderIdAndLastStatus(workOrderId, QuotationStatus.DRAFT)
                .map(QuotationMapper::toDomain);
    }

    @Override
    public PageResponse<ListQuotationResponse> findAllByFilter(QuotationFilter filter, boolean includeDrafts, PageQuery pageQuery) {
        Slice<ListQuotationResponse> results = this.quotationJpaRepository.findAllQuotations(filter.workOrderId(), filter.customerId(), filter.vehicleId(),
                                                                                              getIfNotNullOrDefault(filter.status(), QuotationStatus::name, null),
                                                                                              includeDrafts,
                                                                                              PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(QuotationMapper::toResponse);

        return PageResponse.from(results);
    }
}
