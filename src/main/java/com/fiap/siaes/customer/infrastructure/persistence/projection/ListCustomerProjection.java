package com.fiap.siaes.customer.infrastructure.persistence.projection;

import java.util.UUID;

public interface ListCustomerProjection {
    UUID getId();

    String getDocument();

    String getName();

    String getPhone();

    String getEmail();
}
