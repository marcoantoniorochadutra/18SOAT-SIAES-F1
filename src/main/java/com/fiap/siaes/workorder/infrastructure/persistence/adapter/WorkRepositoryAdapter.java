package com.fiap.siaes.workorder.infrastructure.persistence.adapter;

import com.fiap.siaes.workorder.domain.model.WorkDomainRepository;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class WorkRepositoryAdapter implements WorkDomainRepository {

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WorkOrder save(WorkOrder workOrder) {
//        var entity = WorkOrderMapper.toEntity(workOrder);
//        var saved = this.workOrderJpaRepository.save(entity);
//        return WorkOrderMapper.toDomain(saved);
        return null;
    }
}
