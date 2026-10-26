package com.fiap.siaes.quotation.domain.repository;

import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.util.Optional;

public interface QuotationRepository extends RepositoryBase<Quotation, QuotationId> {

    default Quotation findByIdOrThrowNotFound(QuotationId id) {
        return this.findById(id)
                .orElseThrow(() -> new QuotationNotFoundException(id.toString()));
    }

    boolean existsOpenByWorkOrderId(WorkOrderId workOrderId);

    boolean existsAwaitingApprovalByWorkOrderId(WorkOrderId workOrderId);

    Optional<Quotation> findDraftByWorkOrderId(WorkOrderId workOrderId);
}
