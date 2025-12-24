package com.rehome.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.AuthRequest;
import com.rehome.main.dto.request.RefreshTokenRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.LoginResponse;
import com.rehome.main.dto.response.TokenRefreshResponse;
import com.rehome.main.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 身份驗證控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * 會員登入 POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody AuthRequest request,
            BindingResult bindingResult) {

        // 驗證密碼必填
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("密碼不得為空"));
        }
        if (request.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("密碼長度至少需要 6 個字元"));
        }

        // 驗證請求參數
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            String errorMessage = (fieldError != null && fieldError.getDefaultMessage() != null)
                    ? fieldError.getDefaultMessage()
                    : "請求參數錯誤";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(errorMessage));
        }

        ApiResponse<LoginResponse> response = authService.login(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * 登出 POST /api/auth/logout
     * JWT 無狀態,前端刪除 token 即可登出
     * 此端點保留以便未來實作 Token 黑名單機制
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 未來可從 Header 提取 token 加入黑名單
        ApiResponse<Void> response = authService.logout(null);
        return ResponseEntity.ok(response);
    }

    /**
     * 驗證帳號是否存在 POST /api/auth/verify-account POST /api/auth/forgot-password
     * (別名)
     */
    @PostMapping({"/verify-account", "/forgot-password"})
    public ResponseEntity<ApiResponse<String>> verifyAccount(
            @Valid @RequestBody AuthRequest request,
            BindingResult bindingResult) {

        // 驗證請求參數
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            String errorMessage = (fieldError != null && fieldError.getDefaultMessage() != null)
                    ? fieldError.getDefaultMessage()
                    : "請求參數錯誤";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(errorMessage));
        }

        ApiResponse<String> response = authService.verifyAccount(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 重設密碼 POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody AuthRequest request,
            BindingResult bindingResult) {

        // 驗證新密碼必填
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("新密碼不得為空"));
        }
        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("新密碼長度至少需要 6 個字元"));
        }

        // 驗證請求參數
        if (bindingResult.hasErrors()) {
            var fieldError = bindingResult.getFieldError();
            String errorMessage = (fieldError != null && fieldError.getDefaultMessage() != null)
                    ? fieldError.getDefaultMessage()
                    : "請求參數錯誤";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(errorMessage));
        }

        ApiResponse<Void> response = authService.resetPassword(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 刷新 Access Token (JWT 模式專用) POST /api/auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        ApiResponse<TokenRefreshResponse> response
                = authService.refreshAccessToken(request.getRefreshToken());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
