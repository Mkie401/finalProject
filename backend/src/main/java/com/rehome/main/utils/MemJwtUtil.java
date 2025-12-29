package com.rehome.main.utils;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class MemJwtUtil {
    
    // 產生 HMAC-SHA256 簽章的金鑰
    @Value("${jwt.secret}")
    private String secret;

    // 有效期毫秒數
    @Value("${jwt.expiration}")
    private long expiration;

    // 取得簽章金鑰
    private Key getSingningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成金鑰
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // 生成 Token
    public String generateToken(String email, Long memberId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("memberId", memberId);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // 驗證 Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 從 Token 取得 Email
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    // 從 Token 取得會員 ID
    public Long getMemberIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Number memberId = (Number) claims.get("memberId");
        return memberId != null ? memberId.longValue() : null;
    }

}
