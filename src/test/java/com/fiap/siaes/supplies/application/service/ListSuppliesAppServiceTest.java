package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.sk.infrastructure.persistence.PageResponses;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.port.SuppliesQueryPort;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase.ListSuppliesCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.fiap.siaes.supplies.SuppliesTestFactory.DESCRIPTION;
import static com.fiap.siaes.supplies.SuppliesTestFactory.SKU;
import static com.fiap.siaes.supplies.SuppliesTestFactory.createSupplies;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Supplies")
class ListSuppliesAppServiceTest {

    @Mock
    private SuppliesQueryPort suppliesQueryPort;

    @InjectMocks
    private ListSuppliesAppService listSuppliesAppService;

    @Test
    @DisplayName("Deve listar as peças/insumos da página com os filtros informados")
    void shouldListSuppliesPageWithFilter() {
        var filter = SuppliesFilter.of(" flt-oleo-001 ", "  " + DESCRIPTION + " ", null);

        var pageQuery = PageQuery.of(1, 2);
        var supplies = List.of(
                SuppliesResponse.from(createSupplies()),
                SuppliesResponse.from(createSupplies()));

        when(this.suppliesQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(supplies)));

        PageResponse<SuppliesResponse> response = this.listSuppliesAppService.execute(ListSuppliesCommand.of(filter, pageQuery));

        assertEquals(supplies, response.content());
        assertEquals(SKU, filter.sku());
        assertEquals(DESCRIPTION, filter.description());
        assertTrue(filter.active());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve listar as peças/insumos inativas quando solicitado")
    void shouldListInactiveSuppliesWhenRequested() {
        var filter = SuppliesFilter.of(null, null, false);
        var pageQuery = PageQuery.of(null, null);
        var supplies = List.of(SuppliesResponse.from(createSupplies(false)));

        when(this.suppliesQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(supplies)));

        PageResponse<SuppliesResponse> response = this.listSuppliesAppService.execute(ListSuppliesCommand.of(filter, pageQuery));

        assertEquals(supplies, response.content());
        assertFalse(filter.active());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhuma peça/insumo atender aos filtros")
    void shouldReturnEmptyPageWhenNoSuppliesMatches() {
        var filter = SuppliesFilter.of(null, "inexistente", null);
        var pageQuery = PageQuery.of(null, null);

        when(this.suppliesQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(List.of(), Pageable.ofSize(PageQuery.DEFAULT_SIZE), false)));

        PageResponse<SuppliesResponse> response = this.listSuppliesAppService.execute(ListSuppliesCommand.of(filter, pageQuery));

        assertTrue(response.content().isEmpty());
        assertEquals(PageQuery.DEFAULT_PAGE, response.page());
        assertEquals(PageQuery.DEFAULT_SIZE, response.size());
        assertFalse(response.hasNext());
    }
}
