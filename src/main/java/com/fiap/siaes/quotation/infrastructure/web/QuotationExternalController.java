package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.auth.infrastructure.security.authorization.PublicEndpoint;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase.DecideQuotationByTokenCommand;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase.GetQuotationByApprovalTokenCommand;
import com.fiap.siaes.quotation.infrastructure.web.openapi.QuotationExternalControllerOpenApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PublicEndpoint
@RequestMapping(QuotationExternalController.ENDPOINT)
@RequiredArgsConstructor
public class QuotationExternalController implements QuotationExternalControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/external/quotations/";

    private final GetQuotationByApprovalTokenUseCase getQuotationByApprovalTokenUseCase;
    private final DecideQuotationByTokenUseCase decideQuotationByTokenUseCase;

    @Override
    @GetMapping("/{token}")
    public QuotationApprovalResponse getByToken(@PathVariable String token) {
        return this.getQuotationByApprovalTokenUseCase.execute(GetQuotationByApprovalTokenCommand.of(token));
    }

    @Override
    @PostMapping("/{token}/decision")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decide(@PathVariable String token, @Valid @RequestBody DecideQuotationByTokenCommand command) {
        this.decideQuotationByTokenUseCase.execute(command.with(token));
    }
}
