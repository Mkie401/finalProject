package com.rehome.main.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 身份驗證請求 DTO（整合登入、驗證帳號、重設密碼）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "帳號不得為空")
    @Email(message = "帳號格式必須為有效的 Email")
    private String account;

    @Size(min = 6, message = "密碼長度至少需要 6 個字元")
    private String password;

    @Size(min = 6, message = "新密碼長度至少需要 6 個字元")
    private String newPassword;
}
