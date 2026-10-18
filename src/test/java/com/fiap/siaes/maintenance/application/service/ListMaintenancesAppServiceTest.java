package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.port.MaintenanceQueryPort;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase.ListMaintenancesCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.DESCRIPTION;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.CODE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Maintenance")
class ListMaintenancesAppServiceTest {

    @Mock
    private MaintenanceQueryPort maintenanceQueryPort;

    @InjectMocks
    private ListMaintenancesAppService listMaintenanceAppService;

    @Test
    @DisplayName("Deve listar os serviços da página com os filtros informados")
    void shouldListMaintenancesPageWithFilter() {
        var filter = MaintenanceFilter.builder()
                .code(" troca-oleo ")
                .description("  " + DESCRIPTION + " ")
                .build();

        var pageQuery = new PageQuery(1, 2);
        var maintenance = List.of(
                MaintenanceResponse.from(createMaintenance()),
                MaintenanceResponse.from(createMaintenance()));

        when(this.maintenanceQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponse.from(new SliceImpl<>(maintenance)));

        PageResponse<MaintenanceResponse> response = this.listMaintenanceAppService.execute(ListMaintenancesCommand.from(filter, pageQuery));

        assertEquals(maintenance, response.content());
        assertEquals(CODE, filter.code());
        assertEquals(DESCRIPTION, filter.description());
        assertTrue(filter.active());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve listar os serviços inativos quando solicitado")
    void shouldListInactiveMaintenanceWhenRequested() {
        var filter = MaintenanceFilter.builder().active(false).build();
        var pageQuery = new PageQuery(null, null);
        var maintenance = List.of(MaintenanceResponse.from(createMaintenance(false)));

        when(this.maintenanceQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponse.from(new SliceImpl<>(maintenance)));

        PageResponse<MaintenanceResponse> response = this.listMaintenanceAppService.execute(ListMaintenancesCommand.from(filter, pageQuery));

        assertEquals(maintenance, response.content());
        assertFalse(filter.active());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum serviço atender aos filtros")
    void shouldReturnEmptyPageWhenNoMaintenanceMatches() {
        var filter = MaintenanceFilter.builder().description("inexistente").build();
        var pageQuery = new PageQuery(null, null);

        when(this.maintenanceQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponse.from(new SliceImpl<>(List.of(), Pageable.ofSize(PageQuery.DEFAULT_SIZE), false)));

        PageResponse<MaintenanceResponse> response = this.listMaintenanceAppService.execute(ListMaintenancesCommand.from(filter, pageQuery));

        assertTrue(response.content().isEmpty());
        assertEquals(PageQuery.DEFAULT_PAGE, response.page());
        assertEquals(PageQuery.DEFAULT_SIZE, response.size());
        assertFalse(response.hasNext());
    }
}
