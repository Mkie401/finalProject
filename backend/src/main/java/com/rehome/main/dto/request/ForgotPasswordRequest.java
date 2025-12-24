package com.rehome.main.dto.request;

import lombok.Data;

//忘記密碼請求 DTO
@Data
public class ForgotPasswordRequest {
    
    //使用者的電子郵件地址
    private String email;
    
    //圖形驗證碼
    private String captcha;
    
    // Session ID，用於驗證圖形驗證碼
    private String sessionId;
}
