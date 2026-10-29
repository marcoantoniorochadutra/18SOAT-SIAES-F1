package com.fiap.siaes.sk.infrastructure.event;

import com.fiap.siaes.customer.domain.event.CustomerDeletedEvent;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Event - SpringDomainEventPublisher")
class SpringDomainEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SpringDomainEventPublisher springDomainEventPublisher;

    @Test
    @DisplayName("Deve publicar cada evento de domínio")
    void shouldPublishEachEvent() {
        var first = new CustomerDeletedEvent(CustomerId.generate(), UserId.generate(), Instant.now());
        var second = new CustomerDeletedEvent(CustomerId.generate(), UserId.generate(), Instant.now());

        this.springDomainEventPublisher.publish(List.of(first, second));

        verify(this.applicationEventPublisher).publishEvent(first);
        verify(this.applicationEventPublisher).publishEvent(second);
    }
}
