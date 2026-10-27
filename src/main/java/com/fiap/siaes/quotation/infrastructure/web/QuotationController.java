package com.fiap.siaes.quotation.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase.CompleteQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase.GetQuotationByIdCommand;
import com.fiap.siaes.quotation.application.usecase.IssueQuotationApprovalLinkUseCase;
import com.fiap.siaes.quotation.application.usecase.IssueQuotationApprovalLinkUseCase.IssueQuotationApprovalLinkCommand;
import com.fiap.siaes.quotation.application.usecase.ListQuotationsUseCase;
import com.fiap.siaes.quotation.application.usecase.ListQuotationsUseCase.ListQuotationsCommand;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase.UpdateQuotationCommand;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.infrastructure.web.openapi.QuotationControllerOpenApi;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
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
@RequestMapping(QuotationController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MECHANIC, deniedRoles = {UserRole.CUSTOMER})
public class QuotationController implements QuotationControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/quotations";

    private final CreateQuotationUseCase createQuotationUseCase;
    private final UpdateQuotationUseCase updateQuotationUseCase;
    private final CompleteQuotationUseCase completeQuotationUseCase;
    private final GetQuotationByIdUseCase getQuotationByIdUseCase;
    private final ListQuotationsUseCase listQuotationsUseCase;
    private final IssueQuotationApprovalLinkUseCase issueQuotationApprovalLinkUseCase;
    private final DecideQuotationByCustomerUseCase decideQuotationByCustomerUseCase;

    @Override
    @PostMapping
    public ResponseEntity<QuotationId> create(@Valid @RequestBody CreateQuotationCommand command,
                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        QuotationId id = this.createQuotationUseCase.execute(command.with(authenticatedUser));
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<QuotationId> update(@PathVariable QuotationId id,
                                              @Valid @RequestBody UpdateQuotationCommand command,
                                              @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        QuotationId updatedId = this.updateQuotationUseCase.execute(command.with(id, authenticatedUser));
        return updatedResponse(ENDPOINT, updatedId);
    }

    @Override
    @PostMapping("/{id}/completion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable QuotationId id) {
        this.completeQuotationUseCase.execute(CompleteQuotationCommand.of(id));
    }

    @Override
    @GetMapping("/{id}")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public QuotationResponse getById(@PathVariable QuotationId id,
                                     @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.getQuotationByIdUseCase.execute(GetQuotationByIdCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public PageResponse<ListQuotationResponse> list(@ParameterObject QuotationFilter filter,
                                                    @Valid @ParameterObject PageQuery pageQuery,
                                                    @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.listQuotationsUseCase.execute(ListQuotationsCommand.of(filter, pageQuery, authenticatedUser));
    }

    @Override
    @PostMapping("/{id}/approvalLink")
    public QuotationApprovalLinkResponse issueApprovalLink(@PathVariable QuotationId id) {
        return this.issueQuotationApprovalLinkUseCase.execute(IssueQuotationApprovalLinkCommand.of(id));
    }

    @Override
    @PatchMapping("/{id}/decision")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Authentication(minimumRole = UserRole.CUSTOMER, deniedRoles = { UserRole.MECHANIC, UserRole.MANAGER, UserRole.ADMIN })
    public void decide(@PathVariable QuotationId id,
                       @Valid @RequestBody DecideQuotationByCustomerCommand command,
                       @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.decideQuotationByCustomerUseCase.execute(command.with(id, authenticatedUser));
    }
}
