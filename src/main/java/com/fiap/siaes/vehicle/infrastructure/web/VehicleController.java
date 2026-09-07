package com.fiap.siaes.vehicle.infrastructure.web;

import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.VehicleId;
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

@Tag(name = "Vehicles", description = "Gestão de veículos")
@RestController
@RequestMapping(VehicleController.ENDPOINT)
@RequiredArgsConstructor
public class VehicleController {

    public static final String ENDPOINT = "/api/v1/vehicles";

    private final CreateVehicleUseCase createVehicleUseCase;
    private final UpdateVehicleUseCase updateVehicleUseCase;
    private final GetVehicleUseCase getVehicleUseCase;
    private final ListVehiclesUseCase listVehiclesUseCase;
    private final DeleteVehicleUseCase deleteVehicleUseCase;

    @Operation(summary = "Cadastra um novo veículo")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado")
    @PostMapping
    public ResponseEntity<VehicleId> create(@RequestBody CreateVehicleCommand command) {
        VehicleId id = this.createVehicleUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Operation(summary = "Atualiza um veículo existente")
    @PutMapping("/{id}")
    public Vehicle update(@PathVariable VehicleId id, @RequestBody UpdateVehicleCommand command) {
        return this.updateVehicleUseCase.execute(id, command);
    }

    @Operation(summary = "Busca um veículo pelo id")
    @GetMapping("/{id}")
    public Vehicle get(@PathVariable VehicleId id) {
        return this.getVehicleUseCase.execute(id);
    }

    @Operation(summary = "Lista todos os veículos")
    @GetMapping
    public List<Vehicle> list() {
        return this.listVehiclesUseCase.execute();
    }

    @Operation(summary = "Remove um veículo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable VehicleId id) {
        this.deleteVehicleUseCase.execute(id);
    }
}
