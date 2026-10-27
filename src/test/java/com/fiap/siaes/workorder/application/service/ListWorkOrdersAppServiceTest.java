package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.infrastructure.persistence.PageResponses;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.port.WorkOrderQueryPort;
import com.fiap.siaes.workorder.application.usecase.ListWorkOrdersUseCase.ListWorkOrdersCommand;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.user.UserTestFactory.authenticatedUserCustomer;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserManager;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Work Orders")
class ListWorkOrdersAppServiceTest {

    @Mock
    private WorkOrderQueryPort workOrderQueryPort;

    @InjectMocks
    private ListWorkOrdersAppService listWorkOrdersAppService;

    @Test
    @DisplayName("Deve listar as ordens de serviço da página com os filtros informados")
    void shouldListWorkOrdersPageWithFilter() {
        var filter = WorkOrderFilter.of(CustomerId.generate(), "529.982.247-25", VehicleId.generate(), WorkOrderStatus.RECEIVED);
        var pageQuery = PageQuery.of(0, 10);
        var workOrders = List.of(ListWorkOrderResponse.builder()
                                         .id(WorkOrderId.generate())
                                         .customer(CustomerReferenceResponse.of(filter.customerId(), "Maria Silva"))
                                         .vehicle(VehicleReferenceResponse.of(filter.vehicleId(), PLATE, "Fiat", "Uno"))
                                         .status(WorkOrderStatus.RECEIVED)
                                         .createdAt(Instant.now())
                                         .build());

        when(this.workOrderQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(workOrders)));

        PageResponse<ListWorkOrderResponse> response = this.listWorkOrdersAppService.execute(
                ListWorkOrdersCommand.of(filter, pageQuery, authenticatedUserManager()));

        assertEquals(workOrders, response.content());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve listar para o cliente apenas as próprias ordens de serviço")
    void shouldListOnlyOwnWorkOrdersForCustomer() {
        var customerId = CustomerId.generate();
        var filter = WorkOrderFilter.of(CustomerId.generate(), null, null, null);
        var pageQuery = PageQuery.of(0, 10);
        PageResponse<ListWorkOrderResponse> page = PageResponses.from(new SliceImpl<>(List.of()));

        when(this.workOrderQueryPort.findAllByFilter(filter.withCustomerId(customerId), pageQuery)).thenReturn(page);

        PageResponse<ListWorkOrderResponse> response = this.listWorkOrdersAppService.execute(
                ListWorkOrdersCommand.of(filter, pageQuery, authenticatedUserCustomer(customerId)));

        assertEquals(page, response);
    }
}
