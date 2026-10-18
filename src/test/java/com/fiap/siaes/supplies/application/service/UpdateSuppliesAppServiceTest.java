package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.exception.SuppliesSkuAlreadyExistsException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Supplies")
class UpdateSuppliesAppServiceTest {

    private static final String NEW_SKU = "FLT-AR-002";
    private static final BigDecimal NEW_UNIT_PRICE = new BigDecimal("30.00");

    @Mock
    private SuppliesRepository suppliesRepository;

    @InjectMocks
    private UpdateSuppliesAppService updateSuppliesAppService;

    @Test
    @DisplayName("Deve atualizar uma peça/insumo com sucesso")
    void shouldUpdateSuppliesSuccessfully() {
        var supplies = createSupplies();
        var command = this.command(supplies.getId());

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        var id = this.updateSuppliesAppService.execute(command);

        Supplies savedSupplies = captureSave(this.suppliesRepository, Supplies.class);

        assertEquals(supplies.getId(), id);
        assertEquals(NEW_SKU, savedSupplies.getSku().getValue());
        assertEquals("Filtro de ar", savedSupplies.getDescription());
        assertEquals(NEW_UNIT_PRICE, savedSupplies.getUnitPrice());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a peça/insumo a ser atualizada não existir")
    void shouldThrowWhenSuppliesToUpdateDoesNotExist() {
        var id = SuppliesId.generate();
        var command = this.command(id);

        when(this.suppliesRepository.findByIdOrThrowNotFound(id))
                .thenThrow(new SuppliesNotFoundException(id.toString()));

        var exception = assertThrows(SuppliesNotFoundException.class,
                () -> this.updateSuppliesAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.suppliesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando o SKU já pertencer a outra peça/insumo")
    void shouldNotUpdateWhenSkuAlreadyBelongsToAnotherSupplies() {
        var supplies = createSupplies();
        var command = this.command(supplies.getId());

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);
        when(this.suppliesRepository.existsBySkuAndIdNot(Sku.recreate(NEW_SKU), supplies.getId())).thenReturn(true);

        var exception = assertThrows(SuppliesSkuAlreadyExistsException.class,
                () -> this.updateSuppliesAppService.execute(command));

        assertExceptionParameters(exception, NEW_SKU);
        verify(this.suppliesRepository, never()).save(any());
    }

    private UpdateSuppliesCommand command(SuppliesId id) {
        return UpdateSuppliesCommand.builder()
                .id(id)
                .sku("flt-ar-002")
                .description("Filtro de ar")
                .unitPrice(NEW_UNIT_PRICE)
                .build();
    }
}
