package com.fiap.siaes.auth.domain.security;

import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;

public interface TokenIssuer {

    String USER_DATA_CLAIM = "userData";

    String issueAccessToken(UserId userId, UserRole role);

    String issueRefreshToken(UserId userId);

    record UserClaim(UserId userId, UserRole role) {
        public static UserClaim of(UserId userId, UserRole role) {
            return new UserClaim(userId, role);
        }
    }
}
