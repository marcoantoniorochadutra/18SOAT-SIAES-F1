package com.fiap.siaes.quotation.application.service.helper;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.inventory.application.usecase.ConsumeStockReservationUseCase;
import com.fiap.siaes.inventory.application.usecase.ConsumeStockReservationUseCase.ConsumeStockReservationCommand;
import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase;
import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase.ReleaseStockReservationCommand;
import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase;
import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase.ReserveStockCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Quotation Stock Synchronizer")
class QuotationStockHelperTest {

    private static final UserId PERFORMED_BY = UserId.generate();
    private static final String CUSTOMER_NAME = "João da Silva";

    @Mock
    private ReserveStockUseCase reserveStockUseCase;

    @Mock
    private ReleaseStockReservationUseCase releaseStockReservationUseCase;

    @Mock
    private ConsumeStockReservationUseCase consumeStockReservationUseCase;

    @InjectMocks
    private QuotationStockHelper quotationStockHelper;

    @Test
    @DisplayName("Deve reservar todas as quantidades quando não houver reserva anterior")
    void shouldReserveAllQuantitiesWhenThereIsNoPreviousReservation() {
        var suppliesId = SuppliesId.generate();
        Quotation quotation = this.quotationWith(List.of(createQuotationSupplies(suppliesId, 2)));

        this.quotationStockHelper.reserve(quotation, PERFORMED_BY);

        verify(this.reserveStockUseCase).execute(ReserveStockCommand.of(suppliesId, 2, quotation.getId().id(), PERFORMED_BY));
        verifyNoInteractions(this.releaseStockReservationUseCase);
    }

    @Test
    @DisplayName("Deve reservar apenas o aumento das quantidades")
    void shouldReserveOnlyIncreasedQuantities() {
        var increasedId = SuppliesId.generate();
        var decreasedId = SuppliesId.generate();
        var removedId = SuppliesId.generate();
        var unchangedId = SuppliesId.generate();
        var addedId = SuppliesId.generate();
        Quotation quotation = this.quotationWith(List.of(createQuotationSupplies(increasedId, 5),
                                                         createQuotationSupplies(decreasedId, 1),
                                                         createQuotationSupplies(unchangedId, 2),
                                                         createQuotationSupplies(addedId, 4)));
        var previous = Map.of(increasedId, 2, decreasedId, 3, removedId, 6, unchangedId, 2);
        var referenceId = quotation.getId().id();

        this.quotationStockHelper.reserve(previous, quotation, PERFORMED_BY);

        verify(this.reserveStockUseCase).execute(ReserveStockCommand.of(increasedId, 3, referenceId, PERFORMED_BY));
        verify(this.reserveStockUseCase).execute(ReserveStockCommand.of(addedId, 4, referenceId, PERFORMED_BY));
        verifyNoMoreInteractions(this.reserveStockUseCase);
        verifyNoInteractions(this.releaseStockReservationUseCase);
    }

    @Test
    @DisplayName("Deve liberar apenas a redução das quantidades")
    void shouldReleaseOnlyDecreasedQuantities() {
        var increasedId = SuppliesId.generate();
        var decreasedId = SuppliesId.generate();
        var removedId = SuppliesId.generate();
        var unchangedId = SuppliesId.generate();
        Quotation quotation = this.quotationWith(List.of(createQuotationSupplies(increasedId, 5),
                                                         createQuotationSupplies(decreasedId, 1),
                                                         createQuotationSupplies(unchangedId, 2)));
        var previous = Map.of(increasedId, 2, decreasedId, 3, removedId, 6, unchangedId, 2);
        var referenceId = quotation.getId().id();

        this.quotationStockHelper.release(previous, quotation, PERFORMED_BY);

        verify(this.releaseStockReservationUseCase).execute(ReleaseStockReservationCommand.builder()
                .suppliesId(decreasedId)
                .quantity(2)
                .referenceId(referenceId)
                .performedBy(PERFORMED_BY)
                .build());
        verify(this.releaseStockReservationUseCase).execute(ReleaseStockReservationCommand.builder()
                .suppliesId(removedId)
                .quantity(6)
                .referenceId(referenceId)
                .performedBy(PERFORMED_BY)
                .build());
        verifyNoMoreInteractions(this.releaseStockReservationUseCase);
        verifyNoInteractions(this.reserveStockUseCase);
    }

    @Test
    @DisplayName("Deve liberar todas as quantidades reservadas do orçamento registrando o nome de quem decidiu pelo link")
    void shouldReleaseAllQuantities() {
        var firstId = SuppliesId.generate();
        var secondId = SuppliesId.generate();
        Quotation quotation = this.quotationWith(List.of(createQuotationSupplies(firstId, 3),
                                                         createQuotationSupplies(secondId, 1)));
        var referenceId = quotation.getId().id();

        this.quotationStockHelper.releaseAll(quotation, null, CUSTOMER_NAME);

        verify(this.releaseStockReservationUseCase).execute(ReleaseStockReservationCommand.builder()
                .suppliesId(firstId)
                .quantity(3)
                .referenceId(referenceId)
                .performedByName(CUSTOMER_NAME)
                .build());
        verify(this.releaseStockReservationUseCase).execute(ReleaseStockReservationCommand.builder()
                .suppliesId(secondId)
                .quantity(1)
                .referenceId(referenceId)
                .performedByName(CUSTOMER_NAME)
                .build());
        verifyNoMoreInteractions(this.releaseStockReservationUseCase);
        verifyNoInteractions(this.reserveStockUseCase);
    }

    @Test
    @DisplayName("Deve dar baixa em todas as quantidades reservadas do orçamento registrando o nome de quem decidiu pelo link")
    void shouldConsumeAllQuantities() {
        var firstId = SuppliesId.generate();
        var secondId = SuppliesId.generate();
        Quotation quotation = this.quotationWith(List.of(createQuotationSupplies(firstId, 3),
                                                         createQuotationSupplies(secondId, 1)));
        var referenceId = quotation.getId().id();

        this.quotationStockHelper.consumeAll(quotation, null, CUSTOMER_NAME);

        verify(this.consumeStockReservationUseCase).execute(ConsumeStockReservationCommand.builder()
                .suppliesId(firstId)
                .quantity(3)
                .referenceId(referenceId)
                .performedByName(CUSTOMER_NAME)
                .build());
        verify(this.consumeStockReservationUseCase).execute(ConsumeStockReservationCommand.builder()
                .suppliesId(secondId)
                .quantity(1)
                .referenceId(referenceId)
                .performedByName(CUSTOMER_NAME)
                .build());
        verifyNoMoreInteractions(this.consumeStockReservationUseCase);
        verifyNoInteractions(this.reserveStockUseCase, this.releaseStockReservationUseCase);
    }

    private Quotation quotationWith(List<QuotationSupplies> supplies) {
        return createQuotation(CustomerId.generate(), QuotationStatus.AWAITING_APPROVAL, supplies);
    }
}
