package com.fiap.siaes.maintenance.infrastructure.web;

import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.DeleteMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.GetMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Services", description = "Gestão do catálogo de serviços")
@RestController
@RequestMapping(MaintenanceController.ENDPOINT)
@RequiredArgsConstructor
public class MaintenanceController {

    public static final String ENDPOINT = "/api/v1/services";

    private final CreateMaintenanceUseCase createMaintenanceUseCase;
    private final UpdateMaintenanceUseCase updateMaintenanceUseCase;
    private final GetMaintenanceUseCase getMaintenanceUseCase;
    private final ListMaintenancesUseCase listMaintenancesUseCase;
    private final DeleteMaintenanceUseCase deleteMaintenanceUseCase;

    @Operation(summary = "Cadastra um novo serviço no catálogo")
    @ApiResponse(responseCode = "201", description = "Serviço cadastrado")
    @PostMapping
    public ResponseEntity<MaintenanceId> create(@RequestBody CreateMaintenanceCommand command) {
        MaintenanceId id = this.createMaintenanceUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Operation(summary = "Atualiza um serviço do catálogo")
    @PutMapping("/{id}")
    public Maintenance update(@PathVariable MaintenanceId id, @RequestBody UpdateMaintenanceCommand command) {
        return this.updateMaintenanceUseCase.execute(id, command);
    }

    @Operation(summary = "Busca um serviço pelo id")
    @GetMapping("/{id}")
    public Maintenance get(@PathVariable MaintenanceId id) {
        return this.getMaintenanceUseCase.execute(id);
    }

    @Operation(summary = "Lista todos os serviços do catálogo")
    @GetMapping
    public List<Maintenance> list() {
        return this.listMaintenancesUseCase.execute();
    }

    @Operation(summary = "Remove um serviço do catálogo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable MaintenanceId id) {
        this.deleteMaintenanceUseCase.execute(id);
    }
}
