package com.rehome.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 相關請求 DTO
 * 用於: refresh-activity, logout, verify-session
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    
    @NotBlank(message = "Token 不得為空")
    private String token;  // Session-based 時使用
    
    private String accessToken;   // JWT 時使用
    private String refreshToken;  // JWT 時使用
}