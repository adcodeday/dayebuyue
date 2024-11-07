package org.lu.zhaodazi.user.service.Impl;

import org.lu.zhaodazi.common.exception.CommonException;
import org.lu.zhaodazi.common.util.JwtUtil;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService{
    public static TokenInfo generate(User user) {
        if(user==null||user.getId()==null){
            throw new CommonException("token-用户为空或用户id为空");
        }
        String uuid = (UUID.randomUUID()).toString();
        RedisUtil.set(user.getId().toString(),uuid,1, TimeUnit.DAYS);
        TokenInfo tokenInfo = new TokenInfo(user.getId(),uuid);
        return tokenInfo;
    }
}
