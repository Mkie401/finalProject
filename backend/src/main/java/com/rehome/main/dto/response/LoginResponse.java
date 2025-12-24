package com.rehome.main.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 登入回應 DTO (純 JWT 模式)
 */
@Data
@Builder
public class LoginResponse {
    // 基本會員資訊
    private Long memberId;
    private String email;
    private String name;
    private String role;

    // JWT Token
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;  // Access Token 有效期(秒)
}
