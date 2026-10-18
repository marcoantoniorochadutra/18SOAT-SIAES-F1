package com.fiap.siaes.maintenance.infrastructure.web;

import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.usecase.ActivateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.DeactivateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.GetMaintenanceByIdUseCase;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase.ListMaintenancesCommand;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.infrastructure.web.openapi.MaintenanceControllerOpenApi;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;
import static com.fiap.siaes.sk.util.ControllerUtils.updatedResponse;

@RestController
@RequestMapping(MaintenanceController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MANAGER)
public class MaintenanceController implements MaintenanceControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/services";

    private final CreateMaintenanceUseCase createMaintenanceUseCase;
    private final UpdateMaintenanceUseCase updateMaintenanceUseCase;
    private final GetMaintenanceByIdUseCase getMaintenanceByIdUseCase;
    private final ListMaintenancesUseCase listMaintenancesUseCase;
    private final DeactivateMaintenanceUseCase deactivateMaintenanceUseCase;
    private final ActivateMaintenanceUseCase activateMaintenanceUseCase;

    @Override
    @PostMapping
    public ResponseEntity<MaintenanceId> create(@Valid @RequestBody CreateMaintenanceCommand command) {
        MaintenanceId id = this.createMaintenanceUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceId> update(@PathVariable MaintenanceId id,
                                             @Valid @RequestBody UpdateMaintenanceCommand command) {
        MaintenanceId updatedId = this.updateMaintenanceUseCase.execute(command.with(id));
        return updatedResponse(ENDPOINT, updatedId);
    }

    @Override
    @GetMapping("/{id}")
    public MaintenanceResponse getById(@PathVariable MaintenanceId id) {
        return this.getMaintenanceByIdUseCase.execute(id);
    }

    @Override
    @GetMapping
    public PageResponse<MaintenanceResponse> list(@ParameterObject MaintenanceFilter filter,
                                               @Valid @ParameterObject PageQuery pageQuery) {
        return this.listMaintenancesUseCase.execute(ListMaintenancesCommand.from(filter, pageQuery));
    }

    @Override
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable MaintenanceId id) {
        this.deactivateMaintenanceUseCase.execute(id);
    }

    @Override
    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable MaintenanceId id) {
        this.activateMaintenanceUseCase.execute(id);
    }
}
