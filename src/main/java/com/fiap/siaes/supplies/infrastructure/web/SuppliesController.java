package com.fiap.siaes.supplies.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.usecase.ActivateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.DeactivateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.GetSuppliesByIdUseCase;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase.ListSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.web.openapi.SuppliesControllerOpenApi;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping(SuppliesController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MANAGER)
public class SuppliesController implements SuppliesControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/supplies";

    private final CreateSuppliesUseCase createSuppliesUseCase;
    private final UpdateSuppliesUseCase updateSuppliesUseCase;
    private final GetSuppliesByIdUseCase getSuppliesByIdUseCase;
    private final ListSuppliesUseCase listSuppliesUseCase;
    private final DeactivateSuppliesUseCase deactivateSuppliesUseCase;
    private final ActivateSuppliesUseCase activateSuppliesUseCase;

    @Override
    @PostMapping
    public ResponseEntity<SuppliesId> create(@Valid @RequestBody CreateSuppliesCommand command,
                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        SuppliesId id = this.createSuppliesUseCase.execute(command.with(authenticatedUser));
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SuppliesId> update(@PathVariable SuppliesId id,
                                             @Valid @RequestBody UpdateSuppliesCommand command) {
        SuppliesId updatedId = this.updateSuppliesUseCase.execute(command.with(id));
        return updatedResponse(ENDPOINT, updatedId);
    }

    @Override
    @GetMapping("/{id}")
    public SuppliesResponse getById(@PathVariable SuppliesId id) {
        return this.getSuppliesByIdUseCase.execute(id);
    }

    @Override
    @GetMapping
    public PageResponse<SuppliesResponse> list(@ParameterObject SuppliesFilter filter,
                                               @Valid @ParameterObject PageQuery pageQuery) {
        return this.listSuppliesUseCase.execute(ListSuppliesCommand.from(filter, pageQuery));
    }

    @Override
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable SuppliesId id) {
        this.deactivateSuppliesUseCase.execute(id);
    }

    @Override
    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable SuppliesId id) {
        this.activateSuppliesUseCase.execute(id);
    }
}
