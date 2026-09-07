package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.diagnosis.application.usecase.CreateDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.DeleteDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.GetDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.ListDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.UpdateDiagnosisUseCase;
import com.fiap.siaes.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.application.usecase.CreateDiagnosisUseCase.CreateDiagnosisCommand;
import com.fiap.siaes.diagnosis.application.usecase.UpdateDiagnosisUseCase.UpdateDiagnosisCommand;
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

@Tag(name = "Diagnosiss", description = "Gestão de veículos")
@RestController
@RequestMapping(DiagnosisController.ENDPOINT)
@RequiredArgsConstructor
public class DiagnosisController {

    public static final String ENDPOINT = "/api/v1/diagnosiss";

    private final CreateDiagnosisUseCase createDiagnosisUseCase;
    private final UpdateDiagnosisUseCase updateDiagnosisUseCase;
    private final GetDiagnosisUseCase getDiagnosisUseCase;
    private final ListDiagnosisUseCase listDiagnosissUseCase;
    private final DeleteDiagnosisUseCase deleteDiagnosisUseCase;

    @Operation(summary = "Cadastra um novo veículo")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado")
    @PostMapping
    public ResponseEntity<DiagnosisId> create(@RequestBody CreateDiagnosisCommand command) {
        DiagnosisId id = this.createDiagnosisUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Operation(summary = "Atualiza um veículo existente")
    @PutMapping("/{id}")
    public Diagnosis update(@PathVariable DiagnosisId id, @RequestBody UpdateDiagnosisCommand command) {
        return this.updateDiagnosisUseCase.execute(id, command);
    }

    @Operation(summary = "Busca um veículo pelo id")
    @GetMapping("/{id}")
    public Diagnosis get(@PathVariable DiagnosisId id) {
        return this.getDiagnosisUseCase.execute(id);
    }

    @Operation(summary = "Lista todos os veículos")
    @GetMapping
    public List<Diagnosis> list() {
        return this.listDiagnosissUseCase.execute();
    }

    @Operation(summary = "Remove um veículo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable DiagnosisId id) {
        this.deleteDiagnosisUseCase.execute(id);
    }
}
