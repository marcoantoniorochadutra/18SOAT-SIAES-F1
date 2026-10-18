package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.domain.exception.SuppliesNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Supplies By Id")
class GetSuppliesByIdAppServiceTest {

    @Mock
    private SuppliesRepository suppliesRepository;

    @InjectMocks
    private GetSuppliesByIdAppService getSuppliesByIdAppService;

    @Test
    @DisplayName("Deve buscar uma peça/insumo pelo id com sucesso")
    void shouldGetSuppliesSuccessfully() {
        var supplies = createSupplies();

        when(this.suppliesRepository.findByIdOrThrowNotFound(supplies.getId())).thenReturn(supplies);

        SuppliesResponse response = this.getSuppliesByIdAppService.execute(supplies.getId());

        assertEquals(supplies.getId(), response.id());
        assertEquals(supplies.getSku().getValue(), response.sku());
        assertEquals(supplies.getDescription(), response.description());
        assertEquals(supplies.getUnitPrice(), response.unitPrice());
        assertEquals(supplies.isActive(), response.active());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a peça/insumo não existir")
    void shouldThrowWhenSuppliesNotFound() {
        var id = SuppliesId.generate();

        when(this.suppliesRepository.findByIdOrThrowNotFound(id)).thenThrow(new SuppliesNotFoundException(id.toString()));

        var exception = assertThrows(SuppliesNotFoundException.class,
                () -> this.getSuppliesByIdAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
    }
}
