package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UserId id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private CustomerId customerId;
}
