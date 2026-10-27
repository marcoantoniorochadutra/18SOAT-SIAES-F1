package com.fiap.siaes.workorder.infrastructure.web;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.utils.ControllerTestUtils;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.usecase.CancelWorkOrderUseCase;
import com.fiap.siaes.workorder.application.usecase.CancelWorkOrderUseCase.CancelWorkOrderCommand;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase.CreateWorkOrderCommand;
import com.fiap.siaes.workorder.application.usecase.DeliverWorkOrderUseCase;
import com.fiap.siaes.workorder.application.usecase.DeliverWorkOrderUseCase.DeliverWorkOrderCommand;
import com.fiap.siaes.workorder.application.usecase.FinishWorkOrderUseCase;
import com.fiap.siaes.workorder.application.usecase.FinishWorkOrderUseCase.FinishWorkOrderCommand;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderByIdUseCase;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderByIdUseCase.GetWorkOrderByIdCommand;
import com.fiap.siaes.workorder.application.usecase.ListWorkOrdersUseCase;
import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase;
import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase.StartWorkOrderDiagnosisCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderOpenQuotationException;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static com.fiap.siaes.workorder.application.dto.WorkOrderResponse.from;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Work Order")
class WorkOrderControllerTest extends ControllerTestUtils {

    @MockitoBean
    private CreateWorkOrderUseCase createWorkOrderUseCase;

    @MockitoBean
    private StartWorkOrderDiagnosisUseCase startWorkOrderDiagnosisUseCase;

    @MockitoBean
    private FinishWorkOrderUseCase finishWorkOrderUseCase;

    @MockitoBean
    private DeliverWorkOrderUseCase deliverWorkOrderUseCase;

    @MockitoBean
    private CancelWorkOrderUseCase cancelWorkOrderUseCase;

    @MockitoBean
    private GetWorkOrderByIdUseCase getWorkOrderByIdUseCase;

    @MockitoBean
    private ListWorkOrdersUseCase listWorkOrdersUseCase;

    @Test
    @DisplayName("[201] Deve criar ordem de serviço com sucesso")
    void shouldCreateWorkOrder() throws Exception {
        var command = CreateWorkOrderCommand.of(CustomerId.generate(), VehicleId.generate());
        var workOrderId = WorkOrderId.generate();

        when(this.createWorkOrderUseCase.execute(command)).thenReturn(workOrderId);

        super.executePostAuthenticated(WorkOrderController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(workOrderId.toString())));

        verify(this.createWorkOrderUseCase).execute(command);
    }

    @Test
    @DisplayName("[400] Não deve criar ordem de serviço sem veículo")
    void shouldNotCreateWorkOrderWithoutVehicle() throws Exception {
        var command = CreateWorkOrderCommand.of(CustomerId.generate(), null);

        super.executePostAuthenticated(WorkOrderController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("WorkOrderCommand.vehicleId.notNull"));

        verifyNoInteractions(this.createWorkOrderUseCase);
    }

    @Test
    @DisplayName("[204] Deve iniciar o diagnóstico da ordem de serviço")
    void shouldStartDiagnosis() throws Exception {
        var id = WorkOrderId.generate();

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/diagnosis", null)
                .andExpect(status().isNoContent());

        verify(this.startWorkOrderDiagnosisUseCase).execute(StartWorkOrderDiagnosisCommand.of(id));
    }

    @Test
    @DisplayName("[409] Não deve iniciar o diagnóstico fora da ordem de serviço recebida")
    void shouldNotStartDiagnosisOutsideReceived() throws Exception {
        var id = WorkOrderId.generate();

        doThrow(new WorkOrderStatusTransitionInvalidException(id.toString(), "IN_EXECUTION", "IN_DIAGNOSIS"))
                .when(this.startWorkOrderDiagnosisUseCase).execute(StartWorkOrderDiagnosisCommand.of(id));

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/diagnosis", null)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[204] Deve finalizar a ordem de serviço")
    void shouldFinishWorkOrder() throws Exception {
        var id = WorkOrderId.generate();

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/completion", null)
                .andExpect(status().isNoContent());

        verify(this.finishWorkOrderUseCase).execute(FinishWorkOrderCommand.of(id));
    }

    @Test
    @DisplayName("[409] Não deve finalizar a ordem de serviço fora da execução")
    void shouldNotFinishWorkOrderOutsideExecution() throws Exception {
        var id = WorkOrderId.generate();

        doThrow(new WorkOrderStatusTransitionInvalidException(id.toString(), "RECEIVED", "FINISHED"))
                .when(this.finishWorkOrderUseCase).execute(FinishWorkOrderCommand.of(id));

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/completion", null)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[204] Deve registrar a entrega da ordem de serviço")
    void shouldDeliverWorkOrder() throws Exception {
        var id = WorkOrderId.generate();

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/delivery", null)
                .andExpect(status().isNoContent());

        verify(this.deliverWorkOrderUseCase).execute(DeliverWorkOrderCommand.of(id));
    }

    @Test
    @DisplayName("[409] Não deve entregar a ordem de serviço que não foi finalizada")
    void shouldNotDeliverWorkOrderNotFinished() throws Exception {
        var id = WorkOrderId.generate();

        doThrow(new WorkOrderStatusTransitionInvalidException(id.toString(), "IN_EXECUTION", "DELIVERED"))
                .when(this.deliverWorkOrderUseCase).execute(DeliverWorkOrderCommand.of(id));

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/delivery", null)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[204] Deve cancelar a ordem de serviço")
    void shouldCancelWorkOrder() throws Exception {
        var id = WorkOrderId.generate();

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/cancellation", null)
                .andExpect(status().isNoContent());

        verify(this.cancelWorkOrderUseCase).execute(CancelWorkOrderCommand.of(id, AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[409] Não deve cancelar a ordem de serviço com orçamento aguardando aprovação")
    void shouldNotCancelWorkOrderWithQuotationAwaitingApproval() throws Exception {
        var id = WorkOrderId.generate();

        doThrow(new WorkOrderOpenQuotationException(id.toString()))
                .when(this.cancelWorkOrderUseCase).execute(CancelWorkOrderCommand.of(id, AUTHENTICATED_USER_ADMIN));

        super.executePostAuthenticated(WorkOrderController.ENDPOINT + "/" + id + "/cancellation", null)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[200] Deve buscar ordem de serviço pelo id")
    void shouldGetWorkOrderById() throws Exception {
        var workOrder = createWorkOrder();

        when(this.getWorkOrderByIdUseCase.execute(GetWorkOrderByIdCommand.of(workOrder.getId(), AUTHENTICATED_USER_ADMIN)))
                .thenReturn(from(workOrder, CustomerReferenceResponse.from(createCustomer()), VehicleReferenceResponse.from(createVehicle())));

        super.executeGetAuthenticated(WorkOrderController.ENDPOINT + "/" + workOrder.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andExpect(jsonPath("$.vehicle.plate").value(PLATE))
                .andExpect(jsonPath("$.statusHistory.length()").value(1));
    }

    @Test
    @DisplayName("[200] Deve listar ordens de serviço com sucesso")
    void shouldListWorkOrders() throws Exception {
        var response = ListWorkOrderResponse.builder()
                .id(WorkOrderId.generate())
                .customer(CustomerReferenceResponse.of(CustomerId.generate(), "Maria Silva"))
                .vehicle(VehicleReferenceResponse.of(VehicleId.generate(), PLATE, "Fiat", "Uno"))
                .status(WorkOrderStatus.RECEIVED)
                .createdAt(Instant.now())
                .build();

        when(this.listWorkOrdersUseCase.execute(any())).thenReturn(new PageResponse<>(List.of(response), false, 0, 20));

        super.executeGetAuthenticated(WorkOrderController.ENDPOINT + "?status=RECEIVED")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id().toString()))
                .andExpect(jsonPath("$.content[0].status").value("RECEIVED"))
                .andExpect(jsonPath("$.content[0].customer.name").value("Maria Silva"))
                .andExpect(jsonPath("$.content[0].vehicle.plate").value(PLATE))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar ordens de serviço com tamanho de página inválido")
    void shouldNotListWorkOrdersWhenPageSizeExceedsLimit() throws Exception {
        super.executeGetAuthenticated(WorkOrderController.ENDPOINT + "?size=101")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"));

        verifyNoInteractions(this.listWorkOrdersUseCase);
    }
}
