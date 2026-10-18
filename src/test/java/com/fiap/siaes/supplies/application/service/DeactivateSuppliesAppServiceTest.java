package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyInactiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Deactivate Supplies")
class DeactivateSuppliesAppServiceTest {

    @Mock
    private SuppliesRepository suppliesRepository;

    @InjectMocks
    private DeactivateSuppliesAppService deactivateSuppliesAppService;

    @Test
    @DisplayName("Deve inativar uma peça/insumo com sucesso")
    void shouldDeactivateSuppliesSuccessfully() {
        var supplies = createSupplies(true);

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        this.deactivateSuppliesAppService.execute(supplies.getId());

        Supplies savedSupplies = captureSave(this.suppliesRepository, Supplies.class);

        assertFalse(savedSupplies.isActive());
    }

    @Test
    @DisplayName("Não deve inativar peça/insumo já inativa")
    void shouldNotDeactivateWhenAlreadyInactive() {
        var supplies = createSupplies(false);
        var suppliesId = supplies.getId();

        when(this.suppliesRepository.findByIdOrThrowNotFound(suppliesId)).thenReturn(supplies);

        var exception = assertThrows(SuppliesAlreadyInactiveException.class,
                () -> this.deactivateSuppliesAppService.execute(suppliesId));

        assertExceptionParameters(exception, supplies.getId().toString());
        verify(this.suppliesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve inativar peça/insumo inexistente")
    void shouldNotDeactivateWhenNotFound() {
        var id = SuppliesId.generate();

        when(this.suppliesRepository.findByIdOrThrowNotFound(id)).thenThrow(new SuppliesNotFoundException(id.toString()));

        var exception = assertThrows(SuppliesNotFoundException.class,
                () -> this.deactivateSuppliesAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
        verify(this.suppliesRepository, never()).save(any());
    }
}
