package com.fiap.siaes.workorder.infrastructure.integration;

import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Integration Adapter - Work Order Quotation")
class WorkOrderQuotationAdapterTest {

    @Mock
    private QuotationRepository quotationRepository;

    @InjectMocks
    private WorkOrderQuotationAdapter workOrderQuotationAdapter;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("Deve informar se a ordem de serviço possui orçamento aguardando aprovação")
    void shouldTellIfWorkOrderHasQuotationAwaitingApproval(boolean awaitingApproval) {
        var workOrderId = WorkOrderId.generate();

        when(this.quotationRepository.existsAwaitingApprovalByWorkOrderId(workOrderId)).thenReturn(awaitingApproval);

        assertEquals(awaitingApproval, this.workOrderQuotationAdapter.hasQuotationAwaitingApproval(workOrderId));
    }
}
