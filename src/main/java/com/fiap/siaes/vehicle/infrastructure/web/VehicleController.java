package com.fiap.siaes.vehicle.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase.DeleteVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase.GetVehicleByIdCommand;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase.ListVehiclesCommand;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.web.openapi.VehicleControllerOpenApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(VehicleController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.CUSTOMER, deniedRoles = {UserRole.MECHANIC})
public class VehicleController implements VehicleControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/vehicles";

    private final CreateVehicleUseCase createVehicleUseCase;
    private final UpdateVehicleUseCase updateVehicleUseCase;
    private final DeleteVehicleUseCase deleteVehicleUseCase;
    private final GetVehicleByIdUseCase getVehicleByIdUseCase;
    private final ListVehiclesUseCase listVehiclesUseCase;

    @Override
    @PostMapping
    public ResponseEntity<VehicleId> create(@Valid @RequestBody CreateVehicleCommand command,
                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        VehicleId id = this.createVehicleUseCase.execute(command.with(authenticatedUser));
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<VehicleId> update(@PathVariable VehicleId id,
                                            @Valid @RequestBody UpdateVehicleCommand command,
                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        VehicleId updatedId = this.updateVehicleUseCase.execute(command.with(id, authenticatedUser));
        return updatedResponse(ENDPOINT, updatedId);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable VehicleId id,
                       @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.deleteVehicleUseCase.execute(DeleteVehicleCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable VehicleId id,
                                   @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.getVehicleByIdUseCase.execute(GetVehicleByIdCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping
    public PageResponse<VehicleResponse> list(@ParameterObject VehicleFilter filter,
                                              @Valid @ParameterObject PageQuery pageQuery,
                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.listVehiclesUseCase.execute(ListVehiclesCommand.of(filter, pageQuery, authenticatedUser));
    }

}
