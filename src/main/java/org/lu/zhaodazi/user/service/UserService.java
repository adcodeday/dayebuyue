package org.lu.zhaodazi.user.service;

import org.lu.zhaodazi.user.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    boolean save(User user);
    User loadUserByUserId(Long userId);

    User loadUserByEmail(String email);
    User loadUserByOpenId(String openid);
    void update(User user);
}
