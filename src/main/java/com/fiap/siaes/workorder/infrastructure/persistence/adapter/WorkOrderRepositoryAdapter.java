package com.fiap.siaes.workorder.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.port.WorkOrderQueryPort;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpaEntity;
import com.fiap.siaes.workorder.infrastructure.persistence.mapper.WorkOrderMapper;
import com.fiap.siaes.workorder.infrastructure.persistence.repository.WorkOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

@Repository
@RequiredArgsConstructor
public class WorkOrderRepositoryAdapter implements WorkOrderRepository, WorkOrderQueryPort {

    private final WorkOrderJpaRepository workOrderJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderJpaEntity entity = WorkOrderMapper.toEntity(workOrder);

        WorkOrderJpaEntity saved = this.workOrderJpaRepository.save(entity);

        return WorkOrderMapper.toDomain(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<WorkOrder> findById(WorkOrderId id) {
        return this.workOrderJpaRepository.findById(id)
                .map(WorkOrderMapper::toDomain);
    }

    @Override
    public boolean existsById(WorkOrderId id) {
        return this.workOrderJpaRepository.existsById(id);
    }

    @Override
    public PageResponse<ListWorkOrderResponse> findAllByFilter(WorkOrderFilter filter, PageQuery pageQuery) {
        Slice<ListWorkOrderResponse> results = this.workOrderJpaRepository.findAllWorkOrders(filter.customerId(), filter.document(), filter.vehicleId(),
                                                                                                getIfNotNullOrDefault(filter.status(), WorkOrderStatus::name, null),
                                                                                                PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(WorkOrderMapper::toResponse);

        return PageResponse.from(results);
    }
}
