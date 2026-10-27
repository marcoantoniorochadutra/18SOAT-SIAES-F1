package com.fiap.siaes.auth.domain.security;

import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;

public interface TokenIssuer {

    String issueAccessToken(UserId userId, UserRole role);

    RefreshToken issueRefreshToken();

    String hashRefreshToken(String value);
}
