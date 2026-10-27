package com.fiap.siaes.vehicle.application.service;


import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.port.VehicleQueryPort;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase.ListVehiclesCommand;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserCustomer;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserManager;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Vehicles")
class ListVehiclesAppServiceTest {

    @Mock
    private VehicleQueryPort vehicleQueryPort;

    @InjectMocks
    private ListVehiclesAppService listVehiclesAppService;

    @Test
    @DisplayName("Deve listar os veículos da página com os filtros informados")
    void shouldListVehiclesPageWithFilter() {
        var filter = VehicleFilter.builder()
                .plate("abc-1d23")
                .brand("  Fiat ")
                .build();

        var pageQuery = PageQuery.of(1, 2);
        var vehicles = List.of(
                VehicleResponse.from(createVehicle(), createCustomer()),
                VehicleResponse.from(createVehicle(), createCustomer()));

        when(this.vehicleQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponse.from(new SliceImpl<>(vehicles)));

        PageResponse<VehicleResponse> response = this.listVehiclesAppService.execute(ListVehiclesCommand.of(filter, pageQuery, authenticatedUserManager()));

        assertEquals(vehicles, response.content());
        assertEquals(PLATE, filter.plate());
        assertEquals("Fiat", filter.brand());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum veículo atender aos filtros")
    void shouldReturnEmptyPageWhenNoVehicleMatches() {
        var filter = VehicleFilter.builder().model("inexistente").build();
        var pageQuery = PageQuery.of(null, null);

        when(this.vehicleQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponse.from(new SliceImpl<>(List.of(), Pageable.ofSize(PageQuery.DEFAULT_SIZE), false)));

        PageResponse<VehicleResponse> response = this.listVehiclesAppService.execute(ListVehiclesCommand.of(filter, pageQuery, authenticatedUserManager()));

        assertTrue(response.content().isEmpty());
        assertEquals(PageQuery.DEFAULT_PAGE, response.page());
        assertEquals(PageQuery.DEFAULT_SIZE, response.size());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve listar para o cliente apenas os próprios veículos")
    void shouldListOnlyOwnVehiclesForCustomer() {
        var customerId = CustomerId.generate();
        var filter = VehicleFilter.builder().customerId(CustomerId.generate()).build();
        var pageQuery = PageQuery.of(0, 10);
        PageResponse<VehicleResponse> page = PageResponse.from(new SliceImpl<>(List.of()));

        when(this.vehicleQueryPort.findAllByFilter(filter.withCustomerId(customerId), pageQuery)).thenReturn(page);

        PageResponse<VehicleResponse> response = this.listVehiclesAppService.execute(
                ListVehiclesCommand.of(filter, pageQuery, authenticatedUserCustomer(customerId)));

        assertEquals(page, response);
    }
}
