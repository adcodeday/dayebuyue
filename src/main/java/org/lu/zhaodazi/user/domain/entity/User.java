package org.lu.zhaodazi.user.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class User implements UserDetails {
    private Long id;

    private String username;

    private String password;

    private String email;

    private String openId;

    private String role;

    private boolean enabled;
    private Integer province;
    private Integer sex;
    private Integer wantProvince;
    private Integer wantSex;
    private String codeWord;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;


    private Collection<GrantedAuthority> authorities;

}
