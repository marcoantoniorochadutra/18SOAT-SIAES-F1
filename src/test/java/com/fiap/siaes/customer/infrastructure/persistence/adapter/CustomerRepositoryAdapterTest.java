package com.fiap.siaes.customer.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.infrastructure.persistence.entity.CustomerJpa;
import com.fiap.siaes.customer.infrastructure.persistence.projection.ListCustomerProjection;
import com.fiap.siaes.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.customer.CustomerTestFactory.createCustomerJpa;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Persistence Adapter - Customer")
class CustomerRepositoryAdapterTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    @InjectMocks
    private CustomerRepositoryAdapter customerRepositoryAdapter;

    @Test
    @DisplayName("Deve salvar o cliente e retornar o agregado reconstruído")
    void shouldSaveCustomer() {
        var customer = createCustomer();
        when(this.customerJpaRepository.save(any(CustomerJpa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        this.customerRepositoryAdapter.save(customer);

        CustomerJpa savedEntity = captureSave(this.customerJpaRepository, CustomerJpa.class);

        assertEquals(customer.getId(), savedEntity.getId());
        assertEquals(customer.getEmail(), savedEntity.getEmail());
        assertEquals(customer.getName(), savedEntity.getName());
        assertEquals(customer.getPhone(), savedEntity.getPhone());
        assertEquals(customer.getDocument().getValue(), savedEntity.getDocument());
    }

    @Test
    @DisplayName("Deve buscar um cliente pelo id")
    void shouldFindCustomerById() {
        var customer = createCustomerJpa();

        when(this.customerJpaRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));

        Customer customerFound = this.customerRepositoryAdapter.findById(customer.getId()).orElse(null);

        assertNotNull(customerFound);
        assertEquals(customer.getId(), customerFound.getId());
        assertEquals(customer.getEmail(), customerFound.getEmail());
        assertEquals(customer.getName(), customerFound.getName());
        assertEquals(customer.getPhone(), customerFound.getPhone());
        assertEquals(customer.getDocument(), customerFound.getDocument().getValue());
    }

    @Test
    @DisplayName("Deve buscar um cliente pelo id e lançar erro quando não encontrado")
    void shouldFindCustomerByIdAndThrowErrorWhenNotFound() {
        var customerId = CustomerId.generate();

        when(this.customerJpaRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> this.customerRepositoryAdapter.findByIdOrThrowNotFound(customerId));
    }

    @Test
    @DisplayName("Deve buscar um cliente pelo id e não lançar erro quando encontrado")
    void shouldFindCustomerByIdAndNotThrowErrorWhenFound() {
        var customer = createCustomerJpa();

        when(this.customerJpaRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));


        Customer customerFound = this.customerRepositoryAdapter.findByIdOrThrowNotFound(customer.getId());

        assertNotNull(customerFound);
        assertEquals(customer.getId(), customerFound.getId());
        assertEquals(customer.getEmail(), customerFound.getEmail());
        assertEquals(customer.getName(), customerFound.getName());
        assertEquals(customer.getPhone(), customerFound.getPhone());
        assertEquals(customer.getDocument(), customerFound.getDocument().getValue());
    }

    @Test
    @DisplayName("Não deve encontrar cliente inexistente")
    void shouldNotFindNonExistentCustomer() {
        var id = CustomerId.generate();

        when(this.customerJpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Customer> found = this.customerRepositoryAdapter.findById(id);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve listar os clientes da página com os filtros informados")
    void shouldFindAllByFilter() {
        var filter = CustomerFilter.builder().name("maria").build();
        var pageQuery = PageQuery.builder().page(0).size(10).build();

        ListCustomerProjection summary = this.createCustomerSummaryFromCustomer(createCustomer());

        when(this.customerJpaRepository.findAllCustomers(
                filter.name(), filter.document(), filter.email(), filter.phone(), PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(summary)));

        PageResponse<CustomerResponse> response = this.customerRepositoryAdapter.findAllByFilter(filter, pageQuery);

        assertThat(response.content()).containsExactly(CustomerResponse.from(summary));
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Deve verificar se já existe cliente com o documento informado")
    void shouldCheckIfDocumentAlreadyExists() {
        var document = Document.create("529.982.247-25");
        when(this.customerJpaRepository.existsByDocument(document.getValue())).thenReturn(true);

        assertThat(this.customerRepositoryAdapter.existsByDocument(document)).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe outro cliente com o documento informado")
    void shouldCheckIfDocumentBelongsToAnotherCustomer() {
        var id = CustomerId.generate();
        when(this.customerJpaRepository.existsByDocumentAndIdNot("52998224725", id)).thenReturn(true);

        assertThat(this.customerRepositoryAdapter.existsByDocumentAndIdNot("52998224725", id)).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe cliente com o e-mail informado")
    void shouldCheckIfEmailAlreadyExists() {
        when(this.customerJpaRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThat(this.customerRepositoryAdapter.existsByEmail("maria@email.com")).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se já existe outro cliente com o e-mail informado")
    void shouldCheckIfEmailBelongsToAnotherCustomer() {
        var id = CustomerId.generate();
        when(this.customerJpaRepository.existsByEmailAndIdNot("maria@email.com", id)).thenReturn(true);

        assertThat(this.customerRepositoryAdapter.existsByEmailAndIdNot("maria@email.com", id)).isTrue();
    }

    @Test
    @DisplayName("Deve remover um cliente pelo id")
    void shouldDeleteCustomerById() {
        var id = CustomerId.generate();

        this.customerRepositoryAdapter.deleteById(id);

        verify(this.customerJpaRepository).deleteById(id);
    }

    private ListCustomerProjection createCustomerSummaryFromCustomer(Customer customer) {
        return new ListCustomerProjection() {
            @Override
            public UUID getId() {
                return customer.getId().id();
            }

            @Override
            public String getDocument() {
                return customer.getDocument().getValue();
            }

            @Override
            public String getName() {
                return customer.getName();
            }

            @Override
            public String getPhone() {
                return customer.getPhone();
            }

            @Override
            public String getEmail() {
                return customer.getEmail();
            }
        };
    }
}
