package org.lu.zhaodazi.auth.authtication;



import org.lu.zhaodazi.user.domain.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
public class WxAuthenticationToken extends AbstractAuthenticationToken {
    //openid
    private Object principal;
    //logincode
    private Object credentials;
    public WxAuthenticationToken(Object principal,Object credentials){
        super(null);
        this.principal = principal;
        this.credentials=credentials;
        this.setAuthenticated(false);
    }

    public WxAuthenticationToken(Object principal,Object credentials,Collection<? extends GrantedAuthority> authorities){
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

    public static WxAuthenticationToken unauthenticated(String principal, Integer credentials) {
        return new WxAuthenticationToken(principal, credentials);
    }

    public static WxAuthenticationToken authenticated(User principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        return new WxAuthenticationToken(principal, credentials, authorities);
    }
}
