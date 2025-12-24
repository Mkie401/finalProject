package com.rehome.main.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rehome.main.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 認證過濾器
 * 攔截每個請求,驗證 JWT Token 並設定 SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 從 Header 提取 Token
            String token = extractTokenFromHeader(request);

            // 驗證 Token
            if (token != null && jwtUtil.validateToken(token)) {
                
                // 確認是 Access Token
                if (jwtUtil.validateTokenType(token, "access")) {
                    
                    // 從 Token 提取資訊
                    Long memberId = jwtUtil.getMemberIdFromToken(token);
                    String email = jwtUtil.getEmailFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    
                    log.info("JWT Token 解析: memberId={}, email={}, role={}", memberId, email, role);

                    // 建立認證物件
                    String authority = "ROLE_" + role.toUpperCase();
                    log.info("建立權限: {}", authority);
                    
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    java.util.List.of(new SimpleGrantedAuthority(authority))
                            );

                    // 設定詳細資訊
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 將會員 ID 存入 request attribute 供後續使用
                    request.setAttribute("memberId", memberId);
                    request.setAttribute("email", email);
                    request.setAttribute("role", role);

                    // 設定到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT 驗證成功: memberId={}, email={}, role={}", memberId, email, role);
                }
            }

        } catch (Exception e) {
            log.error("JWT 驗證失敗: {}", e.getMessage());
        }

        // 繼續過濾鏈
        filterChain.doFilter(request, response);
    }

    /**
     * 從 Authorization Header 提取 Token
     * 格式: "Bearer {token}"
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
