package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.QuotationTestFactory;
import com.fiap.siaes.quotation.application.service.assembler.QuotationAssembler;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationAlreadyOpenException;
import com.fiap.siaes.quotation.domain.exception.QuotationWorkOrderStatusInvalidException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.quotation.QuotationTestFactory.ODOMETER;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Quotation")
class CreateQuotationAppServiceTest {

    private static final AuthenticatedUser AUTHENTICATED_USER = UserTestFactory.authenticatedUserManager();

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private QuotationAssembler quotationAssembler;

    @Mock
    private QuotationStockHelper quotationStockHelper;

    @InjectMocks
    private CreateQuotationAppService createQuotationAppService;

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"IN_DIAGNOSIS", "AWAITING_APPROVAL"})
    @DisplayName("Deve criar um orçamento em rascunho a partir da ordem de serviço e reservar os insumos")
    void shouldCreateQuotationSuccessfully(WorkOrderStatus workOrderStatus) {
        WorkOrder workOrder = createWorkOrder(workOrderStatus);
        var command = this.command(workOrder.getId());
        Quotation quotation = QuotationTestFactory.createNewQuotation();

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.quotationAssembler.toQuotation(command, workOrder)).thenReturn(quotation);

        QuotationId quotationId = this.createQuotationAppService.execute(command);

        assertEquals(quotation.getId(), quotationId);
        assertEquals(workOrderStatus, workOrder.getLastStatus());
        verify(this.quotationRepository).save(quotation);
        verify(this.quotationStockHelper).reserve(quotation, AUTHENTICATED_USER.id());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"IN_DIAGNOSIS", "AWAITING_APPROVAL"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve criar orçamento quando a ordem de serviço não estiver em diagnóstico nem aguardando aprovação")
    void shouldNotCreateQuotationWhenWorkOrderDoesNotAcceptQuotation(WorkOrderStatus workOrderStatus) {
        WorkOrder workOrder = createWorkOrder(workOrderStatus);
        var command = this.command(workOrder.getId());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        var exception = assertThrows(QuotationWorkOrderStatusInvalidException.class,
                () -> this.createQuotationAppService.execute(command));

        assertExceptionParameters(exception, workOrder.getId().toString(), workOrderStatus.name());
        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.quotationAssembler, this.quotationStockHelper);
    }

    @Test
    @DisplayName("Não deve criar orçamento quando a ordem de serviço já possuir orçamento em aberto")
    void shouldNotCreateQuotationWhenWorkOrderHasOpenQuotation() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);
        var command = this.command(workOrder.getId());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.quotationRepository.existsOpenByWorkOrderId(workOrder.getId())).thenReturn(true);

        var exception = assertThrows(QuotationAlreadyOpenException.class,
                () -> this.createQuotationAppService.execute(command));

        assertExceptionParameters(exception, workOrder.getId().toString());
        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.quotationAssembler, this.quotationStockHelper);
    }

    @Test
    @DisplayName("Não deve criar orçamento quando a ordem de serviço não existir")
    void shouldNotCreateQuotationWhenWorkOrderDoesNotExist() {
        var command = this.command(WorkOrderId.generate());

        when(this.workOrderRepository.findByIdOrThrowNotFound(command.workOrderId()))
                .thenThrow(new WorkOrderNotFoundException(command.workOrderId().toString()));

        assertThrows(WorkOrderNotFoundException.class, () -> this.createQuotationAppService.execute(command));

        verifyNoInteractions(this.quotationRepository, this.quotationAssembler, this.quotationStockHelper);
    }

    private CreateQuotationCommand command(WorkOrderId workOrderId) {
        return CreateQuotationCommand.builder()
                .authenticatedUser(AUTHENTICATED_USER)
                .workOrderId(workOrderId)
                .description(DESCRIPTION)
                .odometer(ODOMETER)
                .build();
    }
}
