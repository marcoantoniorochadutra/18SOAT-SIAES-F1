package com.fiap.siaes.user.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.user.domain.model.UserId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpa {

    private UserId id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private CustomerId customerId;
}
