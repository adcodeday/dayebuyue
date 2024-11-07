package org.lu.zhaodazi.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lu.zhaodazi.common.ApplicationConfig;
import org.lu.zhaodazi.common.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
public class JwtUtil {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    private static String secret;
    private static final String UID_CLAIM = "uid";
    private static final String CREATE_TIME = "createTime";

    static {
        ApplicationConfig bean = SpringUtil.getBean(ApplicationConfig.class);
        secret=bean.JWTSecret;
    }

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     */
    public static String createToken(Long uid) {
        // build token
        String token = JWT.create()
                .withClaim(UID_CLAIM, uid) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret)); // signature
        return token;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            throw new CommonException("token-验证异常-"+token);
        }
    }


    /**
     * 根据Token获取uid
     *
     * @param token
     * @return uid
     */
    public static Long getUidOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(UID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }

}

