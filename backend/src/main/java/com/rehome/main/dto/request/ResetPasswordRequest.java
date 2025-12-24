package com.rehome.main.dto.request;

import lombok.Data;

//重設密碼請求 DTO
@Data
public class ResetPasswordRequest {
    
    //重設密碼的 Token (從郵件連結中取得)
    private String token;
    
    //使用者設定的新密碼
    private String newPassword;
    
    //圖形驗證碼
    private String captcha;
    
    //Session ID，用於驗證圖形驗證碼
    private String sessionId;
}
