package com.fiap.siaes.sk.domain.event;

import java.util.List;

public interface DomainEventPublisher {

    void publish(List<DomainEvent> events);
}
