package com.fiap.siaes.workorder.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.WorkOrderExecutionTimeResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderResponse;
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
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderExecutionTimeUseCase;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderExecutionTimeUseCase.GetWorkOrderExecutionTimeCommand;
import com.fiap.siaes.workorder.application.usecase.ListWorkOrdersUseCase;
import com.fiap.siaes.workorder.application.usecase.ListWorkOrdersUseCase.ListWorkOrdersCommand;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderDiagnosisUseCase;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderDiagnosisUseCase.RegisterWorkOrderDiagnosisCommand;
import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase;
import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase.StartWorkOrderDiagnosisCommand;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.infrastructure.web.openapi.WorkOrderControllerOpenApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@RestController
@RequestMapping(WorkOrderController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MECHANIC)
public class WorkOrderController implements WorkOrderControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/workOrders";

    private final CreateWorkOrderUseCase createWorkOrderUseCase;
    private final StartWorkOrderDiagnosisUseCase startWorkOrderDiagnosisUseCase;
    private final RegisterWorkOrderDiagnosisUseCase registerWorkOrderDiagnosisUseCase;
    private final FinishWorkOrderUseCase finishWorkOrderUseCase;
    private final DeliverWorkOrderUseCase deliverWorkOrderUseCase;
    private final CancelWorkOrderUseCase cancelWorkOrderUseCase;
    private final GetWorkOrderByIdUseCase getWorkOrderByIdUseCase;
    private final ListWorkOrdersUseCase listWorkOrdersUseCase;
    private final GetWorkOrderExecutionTimeUseCase getWorkOrderExecutionTimeUseCase;

    @Override
    @PostMapping
    public ResponseEntity<WorkOrderId> create(@Valid @RequestBody CreateWorkOrderCommand command) {
        WorkOrderId id = this.createWorkOrderUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PostMapping("/{id}/diagnosis")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startDiagnosis(@PathVariable WorkOrderId id) {
        this.startWorkOrderDiagnosisUseCase.execute(StartWorkOrderDiagnosisCommand.of(id));
    }

    @Override
    @PutMapping("/{id}/diagnosis")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerDiagnosis(@PathVariable WorkOrderId id,
                                  @Valid @RequestBody RegisterWorkOrderDiagnosisCommand command,
                                  @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.registerWorkOrderDiagnosisUseCase.execute(command.with(id, authenticatedUser));
    }

    @Override
    @PostMapping("/{id}/completion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finish(@PathVariable WorkOrderId id) {
        this.finishWorkOrderUseCase.execute(FinishWorkOrderCommand.of(id));
    }

    @Override
    @PostMapping("/{id}/delivery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliver(@PathVariable WorkOrderId id) {
        this.deliverWorkOrderUseCase.execute(DeliverWorkOrderCommand.of(id));
    }

    @Override
    @PostMapping("/{id}/cancellation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable WorkOrderId id, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.cancelWorkOrderUseCase.execute(CancelWorkOrderCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping("/{id}")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public WorkOrderResponse getById(@PathVariable WorkOrderId id,
                                     @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.getWorkOrderByIdUseCase.execute(GetWorkOrderByIdCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public PageResponse<ListWorkOrderResponse> list(@ParameterObject WorkOrderFilter filter,
                                                    @Valid @ParameterObject PageQuery pageQuery,
                                                    @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.listWorkOrdersUseCase.execute(ListWorkOrdersCommand.of(filter, pageQuery, authenticatedUser));
    }

    @Override
    @GetMapping("/metrics/executionTime")
    @Authentication(minimumRole = UserRole.MANAGER)
    public WorkOrderExecutionTimeResponse getExecutionTime(@RequestParam(required = false) Instant from,
                                                           @RequestParam(required = false) Instant to) {
        return this.getWorkOrderExecutionTimeUseCase.execute(GetWorkOrderExecutionTimeCommand.of(from, to));
    }
}
