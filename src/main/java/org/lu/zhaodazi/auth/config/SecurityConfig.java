package org.lu.zhaodazi.auth.config;

import org.lu.zhaodazi.auth.provider.EmailAuthenticationProvider;
import org.lu.zhaodazi.auth.provider.WxAuthenticationProvider;
import org.lu.zhaodazi.user.service.TokenService;
import org.lu.zhaodazi.user.service.UserService;
import org.lu.zhaodazi.websocket.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserService userService;
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://101.43.22.115")); // 根据实际需求设置允许的源
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 允许的请求方法
        corsConfiguration.setAllowedHeaders(Arrays.asList("*")); // 允许的请求头，可以更严格地定义
        corsConfiguration.setAllowCredentials(true); // 是否允许携带凭证
        corsConfiguration.setMaxAge(3600L); // 预检请求的有效期，单位为秒
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
    @Bean
    public EmailAuthenticationProvider emailAuthenticationProvider() {
        return new EmailAuthenticationProvider(userService);
    }
    @Bean
    public WxAuthenticationProvider wxAuthenticationProvider(){
        return new WxAuthenticationProvider(userService);
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(emailAuthenticationProvider());
        authenticationProviders.add(wxAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(authenticationProviders);
        return authenticationManager;
    }
}