package org.lu.zhaodazi.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {
    @Value("${zhaodazi.jwt.secret}")
    public String JWTSecret;
}
