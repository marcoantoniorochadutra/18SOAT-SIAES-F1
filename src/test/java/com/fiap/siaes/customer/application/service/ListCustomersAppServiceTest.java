package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.port.CustomerQueryPort;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase.ListCustomersCommand;
import com.fiap.siaes.sk.infrastructure.persistence.PageResponses;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Customers")
class ListCustomersAppServiceTest {

    @Mock
    private CustomerQueryPort customerQueryPort;

    @InjectMocks
    private ListCustomersAppService listCustomersAppService;

    @Test
    @DisplayName("Deve listar os clientes da página com os filtros informados")
    void shouldListCustomersPageWithFilter() {
        var filter = CustomerFilter.of("silva", "12.6", null, null);

        var pageQuery = PageQuery.of(1, 2);
        var customers = List.of(
                CustomerResponse.from(createCustomer()),
                CustomerResponse.from(createCustomer()));

        when(this.customerQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(customers)));

        PageResponse<CustomerResponse> response = this.listCustomersAppService.execute(ListCustomersCommand.of(filter, pageQuery));

        assertEquals(customers, response.content());
        assertEquals("126", filter.document());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum cliente atender aos filtros")
    void shouldReturnEmptyPageWhenNoCustomerMatches() {
        var filter = CustomerFilter.of("inexistente", null, null, null);
        var pageQuery = PageQuery.of(null, null);
        when(this.customerQueryPort.findAllByFilter(filter, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(List.of(), Pageable.ofSize(PageQuery.DEFAULT_SIZE), false)));

        PageResponse<CustomerResponse> response = this.listCustomersAppService.execute(ListCustomersCommand.of(filter, pageQuery));

        assertTrue(response.content().isEmpty());
        assertEquals(PageQuery.DEFAULT_PAGE, response.page());
        assertEquals(PageQuery.DEFAULT_SIZE, response.size());
        assertFalse(response.hasNext());
    }
}
