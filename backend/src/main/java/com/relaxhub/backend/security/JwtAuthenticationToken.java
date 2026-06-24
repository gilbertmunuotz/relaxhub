package com.relaxhub.backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser principal;

    public JwtAuthenticationToken(Long userId, String email) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = new AuthenticatedUser(userId, email);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return principal.email();
    }

    public Long getUserId() {
        return principal.userId();
    }

    public String getEmail() {
        return principal.email();
    }
}
