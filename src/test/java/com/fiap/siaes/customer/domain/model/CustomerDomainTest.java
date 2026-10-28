package com.fiap.siaes.customer.domain.model;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.exception.CustomerNameRequiredException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("UnitTest - Domain - Customer")
class CustomerDomainTest {

    @Test
    @DisplayName("Deve criar um novo cliente com sucesso")
    void newCustomerStartsWithSameCreatedAndUpdatedAt() {
        Customer customer = Customer.builder()
                .id(CustomerId.generate())
                .document("529.982.247-25")
                .name("Maria")
                .email("email@email.com")
                .build();

        assertEquals("52998224725", customer.getDocument().getValue());
        assertEquals("Maria", customer.getName());
        assertEquals("email@email.com", customer.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("Não deve criar cliente sem nome")
    void shouldNotCreateCustomerWithoutName(String name) {
        var builder = Customer.builder().document("529.982.247-25").name(name);

        assertThrows(CustomerNameRequiredException.class, builder::build);
    }

    @Test
    @DisplayName("Não deve alterar o cliente quando o nome informado na atualização for vazio")
    void shouldNotUpdateCustomerWithoutName() {
        Customer customer = createCustomer();
        String previousName = customer.getName();

        assertThrows(CustomerNameRequiredException.class,
                     () -> customer.update(" ", customer.getPhone(), customer.getEmail(), "529.982.247-25"));

        assertEquals(previousName, customer.getName());
    }

    @Test
    @DisplayName("Deve registrar o evento de exclusão do cliente")
    void shouldRegisterCustomerDeletedEvent() {
        Customer customer = createCustomer();
        var deletedBy = UserId.generate();

        customer.delete(deletedBy);

        var events = customer.pullDomainEvents();
        assertEquals(1, events.size());
        var event = assertInstanceOf(CustomerDeletedEvent.class, events.getFirst());
        assertEquals(customer.getId(), event.customerId());
        assertEquals(deletedBy, event.deletedBy());
    }
}
