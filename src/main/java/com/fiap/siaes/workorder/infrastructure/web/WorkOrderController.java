package com.fiap.siaes.workorder.infrastructure.web;

import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase.CreateWorkOrderCommand;
import com.fiap.siaes.workorder.domain.model.WorkOrderId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Work Orders", description = "Gestão de ordens de serviço")
@RestController
@RequestMapping(WorkOrderController.ENDPOINT)
@RequiredArgsConstructor
public class WorkOrderController {

    public static final String ENDPOINT = "/api/v1/workOrders/";

    private final CreateWorkOrderUseCase createWorkOrderUseCase;

    @Operation(summary = "Cria uma nova ordem de serviço")
    @ApiResponse(responseCode = "201", description = "Ordem de serviço criada")
    @PostMapping
    public ResponseEntity<WorkOrderId> create(@RequestBody CreateWorkOrderCommand command) {
        WorkOrderId id = this.createWorkOrderUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }
}
