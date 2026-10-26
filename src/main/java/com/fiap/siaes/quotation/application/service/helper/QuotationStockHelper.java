package com.fiap.siaes.quotation.application.service.helper;

import com.fiap.siaes.inventory.application.usecase.ConsumeStockReservationUseCase;
import com.fiap.siaes.inventory.application.usecase.ConsumeStockReservationUseCase.ConsumeStockReservationCommand;
import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase;
import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase.ReleaseStockReservationCommand;
import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase;
import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase.ReserveStockCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuotationStockHelper {

    private static final int ZERO = 0;

    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseStockReservationUseCase releaseStockReservationUseCase;
    private final ConsumeStockReservationUseCase consumeStockReservationUseCase;

    public void reserve(Quotation quotation, UserId performedBy) {
        this.reserve(Map.of(), quotation, performedBy);
    }

    public void reserve(Map<SuppliesId, Integer> previousQuantities, Quotation quotation, UserId performedBy) {
        UUID referenceId = quotation.getId().id();

        quotation.getSuppliesQuantities().forEach((suppliesId, currentQuantity) -> {
            int reserved = currentQuantity - previousQuantities.getOrDefault(suppliesId, ZERO);

            if (reserved > 0) {
                var reserveStockCommand = ReserveStockCommand.of(suppliesId, reserved, referenceId, performedBy);

                this.reserveStockUseCase.execute(reserveStockCommand);
            }
        });
    }

    public void release(Map<SuppliesId, Integer> previousQuantities, Quotation quotation, UserId performedBy) {
        this.release(previousQuantities, quotation.getSuppliesQuantities(), quotation.getId().id(), performedBy, null);
    }

    public void releaseAll(Quotation quotation, UserId performedBy, String performedByName) {
        this.release(quotation.getSuppliesQuantities(), Map.of(), quotation.getId().id(), performedBy, performedByName);
    }

    public void consumeAll(Quotation quotation, UserId performedBy, String performedByName) {
        UUID referenceId = quotation.getId().id();

        quotation.getSuppliesQuantities().forEach((suppliesId, quantity) -> {
            var consumeReservationCommand = ConsumeStockReservationCommand.builder()
                    .suppliesId(suppliesId)
                    .quantity(quantity)
                    .referenceId(referenceId)
                    .performedBy(performedBy)
                    .performedByName(performedByName)
                    .build();

            this.consumeStockReservationUseCase.execute(consumeReservationCommand);
        });
    }

    private void release(Map<SuppliesId, Integer> previousQuantities, Map<SuppliesId, Integer> currentQuantities,
                         UUID referenceId, UserId performedBy, String performedByName) {
        previousQuantities.forEach((suppliesId, previousQuantity) -> {
            int released = previousQuantity - currentQuantities.getOrDefault(suppliesId, ZERO);

            if (released > 0) {
                var releaseReservationCommand = ReleaseStockReservationCommand.builder()
                        .suppliesId(suppliesId)
                        .quantity(released)
                        .referenceId(referenceId)
                        .performedBy(performedBy)
                        .performedByName(performedByName)
                        .build();

                this.releaseStockReservationUseCase.execute(releaseReservationCommand);
            }
        });
    }
}
