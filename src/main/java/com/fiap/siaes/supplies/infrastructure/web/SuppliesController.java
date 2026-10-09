package com.fiap.siaes.supplies.infrastructure.web;

import com.fiap.siaes.supplies.application.usecase.AdjustSuppliesStockUseCase;
import com.fiap.siaes.supplies.application.usecase.AdjustSuppliesStockUseCase.AdjustSuppliesStockCommand;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.DeleteSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.GetSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Suppliess", description = "Gestão de peças e insumos, com controle de estoque")
@RestController
@RequestMapping(SuppliesController.ENDPOINT)
@RequiredArgsConstructor
public class SuppliesController {

    public static final String ENDPOINT = "/api/v1/suppliess";

    private final CreateSuppliesUseCase createSuppliesUseCase;
    private final UpdateSuppliesUseCase updateSuppliesUseCase;
    private final GetSuppliesUseCase getSuppliesUseCase;
    private final ListSuppliesUseCase listSuppliessUseCase;
    private final DeleteSuppliesUseCase deleteSuppliesUseCase;
    private final AdjustSuppliesStockUseCase adjustSuppliesStockUseCase;

    @Operation(summary = "Cadastra uma nova peça/insumo")
    @ApiResponse(responseCode = "201", description = "Peça/insumo cadastrado")
    @PostMapping
    public ResponseEntity<SuppliesId> create(@RequestBody CreateSuppliesCommand command) {
        SuppliesId id = this.createSuppliesUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Operation(summary = "Atualiza uma peça/insumo existente")
    @PutMapping("/{id}")
    public Supplies update(@PathVariable SuppliesId id, @RequestBody UpdateSuppliesCommand command) {
        return this.updateSuppliesUseCase.execute(id, command);
    }

    @Operation(summary = "Busca uma peça/insumo pelo id")
    @GetMapping("/{id}")
    public Supplies get(@PathVariable SuppliesId id) {
        return this.getSuppliesUseCase.execute(id);
    }

    @Operation(summary = "Lista todas as peças/insumos")
    @GetMapping
    public List<Supplies> list() {
        return this.listSuppliessUseCase.execute();
    }

    @Operation(summary = "Ajusta o estoque de uma peça/insumo (positivo repõe, negativo baixa)")
    @PatchMapping("/{id}/stock")
    public Supplies adjustStock(@PathVariable SuppliesId id, @RequestBody AdjustSuppliesStockCommand command) {
        return this.adjustSuppliesStockUseCase.execute(id, command);
    }

    @Operation(summary = "Remove uma peça/insumo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable SuppliesId id) {
        this.deleteSuppliesUseCase.execute(id);
    }
}
