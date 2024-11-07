package org.lu.zhaodazi.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.common.exception.CommonException;
import org.lu.zhaodazi.common.service.MailService;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.regex.Pattern;
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    private static final SecurityContext context= SecurityContextHolder.getContext();

    @Autowired
    JavaMailSender mailSender;

    @Override
    @Async
    public void sendVerifyCode(String email) {
        try {
            String code = generateRandomNumber(6);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("验证码");
            message.setText(code);
            message.setFrom("17516485572@163.com");
            mailSender.send(message);
            RedisUtil.set("EMAIL_CODE:"+email,code,300);
            log.info("邮箱-发送成功-"+email+"-"+code);

        }
        catch (Exception e) {
            throw new CommonException("邮箱-发送失败-"+email);
        }
    }

    public static String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 生成0到9之间的随机数并追加到字符串
        }

        return sb.toString();
    }
}
