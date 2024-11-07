package org.lu.zhaodazi.auth.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lu.zhaodazi.common.exception.CommonException;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.auth.authtication.EmailAuthenticationToken;
import org.lu.zhaodazi.user.service.Impl.TokenServiceImpl;
import org.lu.zhaodazi.user.service.TokenService;
import org.lu.zhaodazi.user.service.UserService;
import org.lu.zhaodazi.user.util.UserUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

//邮箱登录逻辑
@Slf4j
@AllArgsConstructor
public class EmailAuthenticationProvider implements AuthenticationProvider {

    UserService userService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        EmailAuthenticationToken emailAuthenticationToken = (EmailAuthenticationToken) authentication;
        String email=(String) emailAuthenticationToken.getPrincipal();
        String code=(String) emailAuthenticationToken.getCredentials();
        User user = userService.loadUserByEmail(email);

        if(user==null){
            user=new User();
            user.setEmail(email);
            userService.save(user);
        }
//
//        String code1 = RedisUtil.getStr("EMAIL_CODE:"+email);
//        if(StringUtils.isBlank(code1) || !code1.equals(code)){
//            throw new CommonException(2,"邮箱登录-验证码不匹配-"+email+"-"+code);
//        }
//        RedisUtil.del("EMAIL_CODE:"+email);
//        if(!RedisUtil.deleteIfExists(email)){
//            throw new CommonException(3,"邮箱登录-并发错误-"+email+"-");
//        }
        EmailAuthenticationToken authenticated = EmailAuthenticationToken.authenticated(UserUtil.clearUserInfo(user),
                TokenServiceImpl.generate(user),
                null);

        authenticated.setDetails(authentication.getDetails());
        log.info("邮箱登录-成功-uid:"+user.getId());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(EmailAuthenticationToken.class);
    }
}
