package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Quotations", description = "Gestão de veículos")
@RestController
@RequestMapping(QuotationController.ENDPOINT)
@RequiredArgsConstructor
public class QuotationController {

    public static final String ENDPOINT = "/api/v1/diagnosiss";

//    private final CreateQuotationUseCase createQuotationUseCase;
//    private final UpdateQuotationUseCase updateQuotationUseCase;
//    private final GetQuotationUseCase getQuotationUseCase;
//    private final ListQuotationUseCase listQuotationsUseCase;
//    private final DeleteQuotationUseCase deleteQuotationUseCase;

    @Operation(summary = "Cadastra um novo veículo")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado")
    @PostMapping
    public ResponseEntity<QuotationId> create() {
//        QuotationId id = this.createQuotationUseCase.execute();
        return createdResponse(ENDPOINT, QuotationId.generate());
    }

    @Operation(summary = "Atualiza um veículo existente")
    @PutMapping("/{id}")
    public Quotation update(@PathVariable QuotationId id) {
//        return this.updateQuotationUseCase.execute(id, command);
        return null;
    }

    @Operation(summary = "Busca um veículo pelo id")
    @GetMapping("/{id}")
    public Quotation get(@PathVariable QuotationId id) {
//        return this.getQuotationUseCase.execute(id);
        return null;
    }

    @Operation(summary = "Lista todos os veículos")
    @GetMapping
    public List<Quotation> list() {
//        return this.listQuotationsUseCase.execute();
        return null;
    }

    @Operation(summary = "Remove um veículo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable QuotationId id) {
//        this.deleteQuotationUseCase.execute(id);
    }
}
