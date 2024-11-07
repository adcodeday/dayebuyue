package org.lu.zhaodazi.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.auth.domain.dto.LoginDTO;
import org.lu.zhaodazi.auth.domain.vo.WxTxtMsgReqVo;
import org.lu.zhaodazi.auth.enums.LoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
public class WxController {
    @Autowired
    AuthController authController;
    @GetMapping(path = "/callback")
    public String check(HttpServletRequest request) throws IOException {
        String echostr = request.getParameter("echostr");

        log.info("回调----"+echostr);
        return echostr;
    }
    @PostMapping(path = "/callback",
            consumes = {"application/xml", "text/xml"},
            produces = "application/xml;charset=utf-8")
    public String callBack(@RequestBody WxTxtMsgReqVo msg) {
        log.info("调用回调函数");
        String content = msg.getContent();
        String openId = msg.getFromUserName();
        Integer code;
        try {
            code = Integer.valueOf(content);

        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        authController.login(new LoginDTO(openId,String.valueOf(code), LoginType.WX));
        return "";
    }
}
