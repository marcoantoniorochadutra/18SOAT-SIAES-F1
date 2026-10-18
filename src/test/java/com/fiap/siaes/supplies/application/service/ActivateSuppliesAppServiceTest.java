package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyActiveException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Activate Supplies")
class ActivateSuppliesAppServiceTest {

    @Mock
    private SuppliesRepository suppliesRepository;

    @InjectMocks
    private ActivateSuppliesAppService activateSuppliesAppService;

    @Test
    @DisplayName("Deve ativar uma peça/insumo com sucesso")
    void shouldActivateSuppliesSuccessfully() {
        var supplies = createSupplies(false);

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        this.activateSuppliesAppService.execute(supplies.getId());

        Supplies savedSupplies = captureSave(this.suppliesRepository, Supplies.class);

        assertTrue(savedSupplies.isActive());
    }

    @Test
    @DisplayName("Não deve ativar peça/insumo já ativa")
    void shouldNotActivateWhenAlreadyActive() {
        var supplies = createSupplies(true);
        var suppliesId = supplies.getId();

        when(this.suppliesRepository.findByIdOrThrowNotFound(suppliesId)).thenReturn(supplies);

        var exception = assertThrows(SuppliesAlreadyActiveException.class,
                () -> this.activateSuppliesAppService.execute(suppliesId));

        assertExceptionParameters(exception, suppliesId.toString());
        verify(this.suppliesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve ativar peça/insumo inexistente")
    void shouldNotActivateWhenNotFound() {
        var id = SuppliesId.generate();

        when(this.suppliesRepository.findByIdOrThrowNotFound(id)).thenThrow(new SuppliesNotFoundException(id.toString()));

        var exception = assertThrows(SuppliesNotFoundException.class,
                () -> this.activateSuppliesAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
        verify(this.suppliesRepository, never()).save(any());
    }
}
