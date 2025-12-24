package com.rehome.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // 啟用方法級別的安全控制
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 設定
            .csrf(csrf -> csrf.disable())
            
            // Session 管理 (JWT 模式下使用 STATELESS)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 授權設定
            .authorizeHttpRequests(auth -> auth
                // 公開端點 - 不需認證
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/verify-account",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/refresh-token",
                    "/api/qna/all",
                    "/api/qna/random/**",
                    "/api/cs/**",
                     "/api/csf/send",
                    // 登入測試
                    "/api/missing/test/login",
                    "/api/public/options",
                                "/#home"

                ).permitAll()
                
                // 靜態資源 - 必須開放，否則無法載入登入頁面
                .requestMatchers(
                    "/frontend/**",
                    "/admin/**",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/assets/**",
                    "/**",
                    "/*.html"
                ).permitAll()
                
                // API 端點權限控制（正式環境）
                 // 暫時改為只需認證，用於測試
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/api/statistics/**").hasRole("ADMIN")
                .requestMatchers("/api/review/**").hasRole("ADMIN")
                .requestMatchers("/api/members/**").hasRole("ADMIN")
                .requestMatchers("/api/banners/**").hasRole("ADMIN")
                .requestMatchers("/api/survey/**").hasRole("ADMIN")
                .requestMatchers("/api/cases/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/api/member/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/api/**").authenticated()  // 其他 API 需要認證
                
                // 其他所有端點需要認證
                .anyRequest().authenticated()
            )
            
            // 加入 JWT 過濾器 (在 UsernamePasswordAuthenticationFilter 之前執行)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 12/5 OAuth2 第三方登入
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/mem/oauth2/login-success", true)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

