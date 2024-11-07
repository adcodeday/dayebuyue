package org.lu.zhaodazi.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.common.service.MailService;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.auth.service.AuthService;
import org.lu.zhaodazi.user.service.TokenService;
import org.lu.zhaodazi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Autowired
    MailService mailService;


    @Override
    public TokenInfo login(AbstractAuthenticationToken authenticationToken) {
        Authentication authenticate;
        if ((authenticate = SecurityContextHolder.getContext().getAuthentication()) == null || authenticate instanceof AnonymousAuthenticationToken) {
            authenticate = authenticationManager.authenticate(authenticationToken);
        }
        if(authenticate==null){
            //登录失败
            return null;
        }
        return (TokenInfo) authenticate.getCredentials();
    }

    @Override
    public boolean verify(TokenInfo tokenInfo) {
        String uuid = RedisUtil.getStr(tokenInfo.getUid().toString());
        if(StrUtil.isNotBlank(uuid)&&uuid.equals(tokenInfo.getUuid())){
            return true;
        }
        return false;
    }

//    public static String generateRandomNumber(int length) {
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < length; i++) {
//            sb.append(random.nextInt(10)); // 生成0到9之间的随机数并追加到字符串
//        }
//
//        return sb.toString();
//    }
}

