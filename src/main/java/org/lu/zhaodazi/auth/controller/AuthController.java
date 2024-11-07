package org.lu.zhaodazi.auth.controller;

import org.lu.zhaodazi.auth.authtication.WxAuthenticationToken;
import org.lu.zhaodazi.common.domain.vo.res.ApiResult;
import org.lu.zhaodazi.common.service.MailService;
import org.lu.zhaodazi.auth.domain.dto.LoginDTO;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.auth.authtication.EmailAuthenticationToken;
import org.lu.zhaodazi.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @Autowired
    MailService mailService;
    @GetMapping("/emailcode")
    public ApiResult<String> emailCode(String email){
        //TODO 异步线程没办法捕获异常，没办法验证有效性
        mailService.sendVerifyCode(email);
        return ApiResult.success();
    }
    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody LoginDTO loginDTO){
        TokenInfo tokenInfo;
        switch (loginDTO.getLoginType()) {
            case EMAIL:
                EmailAuthenticationToken emailAuthenticationToken = EmailAuthenticationToken.unauthenticated(loginDTO.getUsername(),loginDTO.getCredential());
                tokenInfo = authService.login(emailAuthenticationToken);
                break;
            case WX:
                WxAuthenticationToken wxAuthenticationToken = WxAuthenticationToken.unauthenticated(loginDTO.getUsername(), Integer.valueOf(loginDTO.getCredential()));
                tokenInfo = authService.login(wxAuthenticationToken);
                break;
            default:
                throw new UnsupportedOperationException("不支持的登录方式:" + loginDTO.getLoginType());
        }
        return ApiResult.success(tokenInfo);
    }
}
