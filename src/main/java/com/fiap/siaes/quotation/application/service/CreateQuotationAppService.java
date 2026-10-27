package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.assembler.QuotationAssembler;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase;
import com.fiap.siaes.quotation.domain.exception.QuotationAlreadyOpenException;
import com.fiap.siaes.quotation.domain.exception.QuotationWorkOrderStatusInvalidException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateQuotationAppService implements CreateQuotationUseCase {

    private final QuotationRepository quotationRepository;
    private final WorkOrderRepository workOrderRepository;
    private final QuotationAssembler quotationAssembler;
    private final QuotationStockHelper quotationStockHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public QuotationId execute(CreateQuotationCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.workOrderId());

        this.validateWorkOrder(workOrder);

        Quotation quotation = this.quotationAssembler.toQuotation(command, workOrder);

        this.quotationRepository.save(quotation);

        this.quotationStockHelper.reserve(quotation, command.authenticatedUser().id());

        return quotation.getId();
    }

    private void validateWorkOrder(WorkOrder workOrder) {
        if (!workOrder.acceptsQuotation())
            throw new QuotationWorkOrderStatusInvalidException(workOrder.getId().toString(), workOrder.getLastStatus().name());

        if (this.quotationRepository.existsOpenByWorkOrderId(workOrder.getId()))
            throw new QuotationAlreadyOpenException(workOrder.getId().toString());
    }
}
