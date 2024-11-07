package org.lu.zhaodazi.auth.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.auth.authtication.EmailAuthenticationToken;
import org.lu.zhaodazi.auth.authtication.WxAuthenticationToken;
import org.lu.zhaodazi.common.exception.CommonException;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.TokenService;
import org.lu.zhaodazi.user.service.UserService;
import org.lu.zhaodazi.user.util.UserUtil;
import org.lu.zhaodazi.websocket.service.WebsocketService;
import org.lu.zhaodazi.websocket.service.impl.WebsocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
@Slf4j
@AllArgsConstructor
public class WxAuthenticationProvider implements AuthenticationProvider {
    UserService userService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxAuthenticationToken wxAuthenticationToken = (WxAuthenticationToken) authentication;
        String openid =  (String) wxAuthenticationToken.getPrincipal();
        Integer code = (Integer) wxAuthenticationToken.getCredentials();
        if(!WebsocketServiceImpl.exist(code)){
            throw new CommonException("微信登录验证码不存在-"+code);
        }
        User user = userService.loadUserByOpenId(openid);
        if(user==null){
            user=new User();
            user.setOpenId(openid);
            userService.save(user);
        }
        //TODO 成功登录，传入认证过的authentication
        WxAuthenticationToken authenticated = WxAuthenticationToken.authenticated(UserUtil.clearUserInfo(user), null, null);
        if(WebsocketServiceImpl.loginSuccess(authenticated,code)){
            log.info("微信登录成功-"+user.getId());
            return authenticated;
        }
        throw new CommonException("微信登录失败-"+user.getId());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(WxAuthenticationToken.class);
    }
}
