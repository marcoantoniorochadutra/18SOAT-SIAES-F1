package com.fiap.siaes.inventory.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.application.usecase.GetStockItemUseCase;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase.ListStockMovementsCommand;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase.MoveStockCommand;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase;
import com.fiap.siaes.inventory.infrastructure.web.openapi.StockControllerOpenApi;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.infrastructure.web.SuppliesController;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.fiap.siaes.sk.util.ControllerUtils.buildUrl;

@RestController
@RequestMapping(StockController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MANAGER)
public class StockController implements StockControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/supplies/{suppliesId}/stock";

    private final GetStockItemUseCase getStockItemUseCase;
    private final MoveStockUseCase moveStockUseCase;
    private final ListStockMovementsUseCase listStockMovementsUseCase;

    @Override
    @GetMapping
    public StockItemResponse get(@PathVariable SuppliesId suppliesId) {
        return this.getStockItemUseCase.execute(suppliesId);
    }

    @Override
    @PatchMapping
    public ResponseEntity<SuppliesId> move(@PathVariable SuppliesId suppliesId,
                                           @Valid @RequestBody MoveStockCommand command,
                                           @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        SuppliesId updatedId = this.moveStockUseCase.execute(command.with(suppliesId, authenticatedUser));
        return ResponseEntity.ok()
                .location(URI.create(buildUrl(SuppliesController.ENDPOINT, updatedId.toString(), "stock")))
                .body(updatedId);
    }

    @Override
    @GetMapping("/movements")
    public PageResponse<StockMovementResponse> listMovements(@PathVariable SuppliesId suppliesId,
                                                             @Valid @ParameterObject PageQuery pageQuery) {
        return this.listStockMovementsUseCase.execute(ListStockMovementsCommand.of(suppliesId, pageQuery));
    }
}
