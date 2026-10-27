package com.fiap.siaes.user;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpaEntity;
import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserTestFactory {

    public static AuthenticatedUser authenticatedUserAdmin() {
        return AuthenticatedUser.of(UserId.generate(), null, "admin@email.com", UserRole.ADMIN);
    }

    public static AuthenticatedUser authenticatedUserManager() {
        return AuthenticatedUser.of(UserId.generate(), null, "manager@email.com", UserRole.MANAGER);
    }

    public static AuthenticatedUser authenticatedUserCustomer() {
        return authenticatedUserCustomer(null);
    }

    public static AuthenticatedUser authenticatedUserCustomer(CustomerId customerId) {
        return AuthenticatedUser.of(UserId.generate(), customerId, "customer@email.com", UserRole.CUSTOMER);
    }

    public static User createUser() {
        return User.builder()
                .id(UserId.generate())
                .name(Faker.instance().name().fullName())
                .email(Faker.instance().internet().emailAddress())
                .password(Password.recreate("hashed-password"))
                .role(UserRole.MANAGER)
                .build();
    }

    public static UserJpaEntity createUserJpaEntity() {
        return new UserJpaEntity(
                UserId.generate(),
                Faker.instance().name().fullName(),
                Faker.instance().internet().emailAddress(),
                "hashed-password",
                UserRole.MANAGER,
                null,
                null,
                null,
                null,
                null);
    }

}
