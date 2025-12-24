package com.rehome.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.AuthRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.LoginResponse;
import com.rehome.main.dto.response.TokenRefreshResponse;
import com.rehome.main.entity.Member;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 身份驗證服務
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 會員登入
     */
    public ApiResponse<LoginResponse> login(AuthRequest request) {
        try {
            Member member = memberRepository.findByEmail(request.getAccount())
                    .orElse(null);

            // 檢查會員是否存在
            if (member == null) {
                return ApiResponse.fail("帳號或密碼錯誤");
            }

            // 檢查會員狀態
            log.info("步驟 4: 檢查會員狀態...");
            if ("block".equals(member.getStatus())) {
                return ApiResponse.fail("此帳號已被停用，請聯絡客服");
            }

            // 驗證密碼
            boolean passwordMatch = passwordEncoder.matches(request.getPassword(), member.getPasswordHash());

            if (!passwordMatch) {
                return ApiResponse.fail("帳號或密碼錯誤");
            }

            // 產生 JWT Token
            String accessToken = jwtUtil.generateAccessToken(
                member.getId(), 
                member.getEmail(), 
                member.getRole()
            );
            String refreshToken = jwtUtil.generateRefreshToken(member.getId());
            Long expiresIn = jwtUtil.getAccessTokenExpiration() / 1000; // 轉換為秒

            // 建立回應資料
            LoginResponse loginData = LoginResponse.builder()
                    .memberId(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .role(member.getRole())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .build();

            return ApiResponse.success("登入成功", loginData);

        } catch (Exception e) {
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 驗證帳號是否存在
     */
    public ApiResponse<String> verifyAccount(AuthRequest request) {
        try {
            boolean exists = memberRepository.existsByEmail(request.getAccount());

            if (exists) {
                // 回傳遮蔽後的 Email
                String maskedEmail = maskEmail(request.getAccount());
                return ApiResponse.success("帳號驗證成功", maskedEmail);
            } else {
                return ApiResponse.fail("此帳號不存在");
            }

        } catch (Exception e) {
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 重設密碼
     */
    @Transactional
    public ApiResponse<Void> resetPassword(AuthRequest request) {
        try {
            // 查詢會員
            Member member = memberRepository.findByEmail(request.getAccount())
                    .orElse(null);

            if (member == null) {
                return ApiResponse.fail("帳號不存在");
            }

            // 更新密碼
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            member.setPasswordHash(encodedPassword);
            memberRepository.save(member);

            return ApiResponse.success("密碼重設成功", null);

        } catch (Exception e) {
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }



    /**
     * 登出 (JWT 模式)
     * JWT 是無狀態的,登出由前端刪除 token 即可
     * 後端只需返回成功訊息
     * 未來可實作 Token 黑名單機制
     */
    public ApiResponse<Void> logout(String token) {
        try {
            // TODO: 可選實作 - 將 token 加入黑名單
            // blacklistService.addToBlacklist(token);
            
            return ApiResponse.success("登出成功", null);

        } catch (Exception e) {
            log.error("登出失敗: {}", e.getMessage());
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }



    /**
     * 刷新 Access Token (JWT 模式專用)
     * 目前使用 Session-based 模式,此方法暫時返回錯誤提示
     * 待整合 JwtUtil 後再實作完整邏輯
     */
    public ApiResponse<TokenRefreshResponse> refreshAccessToken(String refreshToken) {
        try {
            // 驗證 Refresh Token
            if (!jwtUtil.validateToken(refreshToken)) {
                return ApiResponse.fail("無效的 Refresh Token");
            }

            // 驗證 Token 類型
            if (!jwtUtil.validateTokenType(refreshToken, "refresh")) {
                return ApiResponse.fail("Token 類型不正確");
            }

            // 提取會員 ID
            Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);

            // 查詢會員資訊
            Member member = memberRepository.findById(memberId)
                    .orElse(null);

            if (member == null) {
                return ApiResponse.fail("會員不存在");
            }

            // 檢查會員狀態
            if ("block".equals(member.getStatus())) {
                return ApiResponse.fail("此帳號已被停用");
            }

            // 產生新的 Access Token
            String newAccessToken = jwtUtil.generateAccessToken(
                    member.getId(),
                    member.getEmail(),
                    member.getRole()
            );

            // 產生新的 Refresh Token
            String newRefreshToken = jwtUtil.generateRefreshToken(member.getId());

            // 建立回應
            TokenRefreshResponse tokenData = new TokenRefreshResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtUtil.getAccessTokenExpiration() / 1000  // 轉換為秒
            );

            return ApiResponse.success("Token 刷新成功", tokenData);

        } catch (Exception e) {
            log.error("刷新 Token 失敗: {}", e.getMessage());
            return ApiResponse.fail("系統錯誤，請稍後再試");
        }
    }

    /**
     * 遮蔽 Email 地址
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return username.charAt(0) + "***@" + domain;
        }

        return username.substring(0, 2) + "***@" + domain;
    }
}
