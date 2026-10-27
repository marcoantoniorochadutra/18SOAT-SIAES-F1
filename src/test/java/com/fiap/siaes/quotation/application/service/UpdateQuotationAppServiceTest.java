package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.service.assembler.QuotationAssembler;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.QuotationSuppliesItemCommand;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase.UpdateQuotationCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationNotEditableException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.UserTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotationSupplies;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Quotation")
class UpdateQuotationAppServiceTest {

    private static final AuthenticatedUser AUTHENTICATED_USER = UserTestFactory.authenticatedUserManager();

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationAssembler quotationAssembler;

    @Mock
    private QuotationStockHelper quotationStockHelper;

    @InjectMocks
    private UpdateQuotationAppService updateQuotationAppService;

    @Test
    @DisplayName("Deve atualizar o orçamento e ajustar as reservas com base nas quantidades anteriores")
    void shouldUpdateQuotationSuccessfully() {
        var previousSuppliesId = SuppliesId.generate();
        var newSuppliesId = SuppliesId.generate();
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT,
                                              List.of(createQuotationSupplies(previousSuppliesId, 3)));
        var command = this.command(quotation.getId(), List.of(QuotationSuppliesItemCommand.of(newSuppliesId, 1)));

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.quotationAssembler.resolveSupplies(command.supplies())).thenReturn(List.of(createQuotationSupplies(newSuppliesId, 1)));
        when(this.quotationAssembler.resolveMaintenances(command.maintenances())).thenReturn(List.of());

        QuotationId quotationId = this.updateQuotationAppService.execute(command);

        Quotation savedQuotation = captureSave(this.quotationRepository, Quotation.class);

        assertEquals(quotation.getId(), quotationId);
        assertEquals("Nova descrição", savedQuotation.getDescription());
        assertEquals(51_000L, savedQuotation.getOdometer());
        assertEquals(Map.of(newSuppliesId, 1), savedQuotation.getSuppliesQuantities());
        verify(this.quotationStockHelper).release(Map.of(previousSuppliesId, 3), savedQuotation, AUTHENTICATED_USER.id());
        verify(this.quotationStockHelper).reserve(Map.of(previousSuppliesId, 3), savedQuotation, AUTHENTICATED_USER.id());
    }

    @Test
    @DisplayName("Não deve atualizar orçamento inexistente")
    void shouldNotUpdateQuotationWhenNotFound() {
        var command = this.command(QuotationId.generate(), List.of());

        when(this.quotationRepository.findByIdOrThrowNotFound(command.id())).thenThrow(new QuotationNotFoundException(command.id().toString()));

        assertThrows(QuotationNotFoundException.class, () -> this.updateQuotationAppService.execute(command));

        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.quotationStockHelper);
    }

    @Test
    @DisplayName("Não deve atualizar orçamento que não aguarda aprovação")
    void shouldNotUpdateQuotationNotAwaitingApproval() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.APPROVED);
        var command = this.command(quotation.getId(), List.of(
                QuotationSuppliesItemCommand.of(SuppliesId.generate(), 1)));

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotEditableException.class, () -> this.updateQuotationAppService.execute(command));

        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.quotationStockHelper);
    }

    private UpdateQuotationCommand command(QuotationId id, List<QuotationSuppliesItemCommand> supplies) {
        return UpdateQuotationCommand.builder()
                .id(id)
                .authenticatedUser(AUTHENTICATED_USER)
                .description("Nova descrição")
                .odometer(51_000L)
                .supplies(supplies)
                .build();
    }
}
