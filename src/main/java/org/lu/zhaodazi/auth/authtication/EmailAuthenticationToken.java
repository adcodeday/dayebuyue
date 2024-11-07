package org.lu.zhaodazi.auth.authtication;

import org.lu.zhaodazi.user.domain.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
public class EmailAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private Object credentials;
    public EmailAuthenticationToken(Object principal,Object credentials){
        super(null);
        this.principal = principal;
        this.credentials=credentials;
        this.setAuthenticated(false);
    }

    public EmailAuthenticationToken(Object principal,Object credentials,Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.credentials=credentials;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public static EmailAuthenticationToken unauthenticated(String principal, String credentials) {
        return new EmailAuthenticationToken(principal, credentials);
    }

    public static EmailAuthenticationToken authenticated(User principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new EmailAuthenticationToken(principal, credentials, authorities);
    }
}
