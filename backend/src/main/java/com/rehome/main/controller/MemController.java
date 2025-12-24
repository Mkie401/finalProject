package com.rehome.main.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.ForgotPasswordRequest;
import com.rehome.main.dto.request.MemAvatarRequest;
import com.rehome.main.dto.request.MemLoginRequest;
import com.rehome.main.dto.request.MemPasswordRequest;
import com.rehome.main.dto.request.MemRequest;
import com.rehome.main.dto.request.ResetPasswordRequest;
import com.rehome.main.dto.response.MemLoginResponse;
import com.rehome.main.dto.response.MemResponse;
import com.rehome.main.service.MemCaptchaService;
import com.rehome.main.service.MemEmailService;
import com.rehome.main.service.MemService;
import com.rehome.main.utils.MemApiResponse;


@RestController
@RequestMapping("/api/mem")
public class MemController {
    @Autowired
    private MemService memService;

    @Autowired
    private MemEmailService memEmailService;

    @Autowired
    private MemCaptchaService memCaptchaService;
    
    // 會員註冊
    @PostMapping("/register")
    public ResponseEntity<MemApiResponse<MemResponse>> register(@RequestBody MemRequest request) {
        try {
            MemResponse response = memService.register(request);
            return ResponseEntity.ok(MemApiResponse.success(response));
        } catch (Exception e) {            
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure(e.getMessage()));
        }
    }

    // 發送 OTP
    @PostMapping("/send-otp")
    public ResponseEntity<MemApiResponse<String>> sendOtp(@RequestBody Map<String, String> request){
        try {
            String email = request.get("email");

            if(email == null || email.trim().isEmpty()){
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("Email 不能空白"));
            }

            memEmailService.memSendConfirmMail(email);

            return ResponseEntity.ok(MemApiResponse.success("OTP 已發送至信箱 " ));

        }catch(Exception e){
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure("發送失敗 : " + e.getMessage()));
        }
    }

    // 驗證 OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<MemApiResponse<String>> verifyOtp(@RequestBody Map<String, String> request){
        try{
            String email = request.get("email");
            String otpCode = request.get("otpCode");

            // 基本驗證
            if(email == null || email.trim().isEmpty() || otpCode == null || otpCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("Email 或 驗證碼 不能空白"));
            }

            boolean isValid = memEmailService.memVerifyOtp(email, otpCode);

            if(isValid){
                return ResponseEntity.ok(MemApiResponse.success("驗證成功"));
            }else{
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("驗證碼錯誤或已過期"));
            }
        
        }catch(Exception e){
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure("驗證失敗 : " + e.getMessage()));
        }
    }

    // 查詢會員基本資料
    @GetMapping("/profile")
    public MemApiResponse<MemResponse> getProfile(@RequestParam Integer memberId) {

        MemResponse response  = memService.getMemberById(memberId);
        return MemApiResponse.success(response);
    }

    // 更新會員暱稱
    @PutMapping("/update/nickName")
    public MemApiResponse<MemResponse> updateNickName(@RequestParam Integer memberId, @RequestBody Map<String, String> request) {

        String nickName = request.get("nickName");

        MemResponse response = memService.updateNickName(memberId, nickName);

        return MemApiResponse.success(response);
    }
    
    // 更新密碼
    @PutMapping("/update/password")
    public ResponseEntity<MemApiResponse<String>> updatePassword(@RequestParam Integer memberId, @RequestBody MemPasswordRequest request) {

        try {
            if(request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("新密碼不能空白"));
            }else{
                memService.updatePassword(memberId, request.getOldPassword(), request.getNewPassword());
                return ResponseEntity.ok(MemApiResponse.success("密碼更新成功"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure(e.getMessage()));
        }
    }

    // 會員登入
    @PostMapping("/login")
    public ResponseEntity<MemLoginResponse> login(@RequestBody MemLoginRequest request) {

        MemLoginResponse response = memService.login(request);

        if(response.getSuccess()) {
            return ResponseEntity.ok(response);
        }else {
            return ResponseEntity.status(401).body(response);
        }
    }

    // 儲存驗證碼
    @PostMapping("/captcha/store")
    public ResponseEntity<Map<String, Object>> storeCaptcha(@RequestBody Map<String, String> request) {
        String captcha = request.get("captcha");
        String sessionId = memCaptchaService.storeCaptcha(captcha);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(response);
    }

    // ========== 重設密碼相關 API ==========
    
    //忘記密碼 - 發送重設密碼郵件
    @PostMapping("/forgot-password")
    public ResponseEntity<MemApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            // 驗證 Email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("Email 不能空白"));
            }

            // 驗證圖形驗證碼
            if (!memCaptchaService.verifyCaptcha(request.getSessionId(), request.getCaptcha())) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("驗證碼錯誤"));
            }
            
            // 處理忘記密碼請求
            memService.requestPasswordReset(request.getEmail());
            
            return ResponseEntity.ok(
                MemApiResponse.success("重設密碼郵件已發送，請檢查您的信箱")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure(e.getMessage()));
        }
    }
    
    //驗證重設密碼 Token 是否有效
    @GetMapping("/verify-reset-token")
    public ResponseEntity<MemApiResponse<Boolean>> verifyResetToken(@RequestParam String token) {
        try {
            boolean isValid = memService.verifyResetToken(token);
            
            if (isValid) {
                return ResponseEntity.ok(
                    MemApiResponse.success(true)
                );
            } else {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("Token 無效或已過期"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure(e.getMessage()));
        }
    }
    
//重設密碼
    @PostMapping("/reset-password")
    public ResponseEntity<MemApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("新密碼不能空白"));
            }

            // 驗證圖形驗證碼
            if (!memCaptchaService.verifyCaptcha(request.getSessionId(), request.getCaptcha())) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("驗證碼錯誤"));
            }
            
            // 重設密碼
            memService.resetPassword(request.getToken(), request.getNewPassword());
            
            return ResponseEntity.ok(
                MemApiResponse.success("密碼重設成功，請使用新密碼登入")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(MemApiResponse.failure(e.getMessage()));
        }
    }

    // 上傳會員頭像
    @PostMapping("/avatar")
    public ResponseEntity<MemApiResponse<String>> uploadAvatar(@RequestBody MemAvatarRequest request) {
        try {
            // 檢查會員
            if(request.getMemberId() == null) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("會員ID不能為空"));
            }

            // 檢查頭像檔案
            if(request.getAvatar() == null ||request.getAvatar().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(MemApiResponse.failure("頭像檔案不能為空"));
            }

            // 儲存頭像
            memService.saveAvatar(request.getMemberId(), request.getAvatar());

            return ResponseEntity.ok(MemApiResponse.success("頭像上傳成功"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(MemApiResponse.failure("上傳失敗 : " + e.getMessage()));
        }
    }

    // 取得會員頭像
    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(@RequestParam Integer memberId) {
        try {
            // 檢查必要參數
            if (memberId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // 從 Service 取得頭像
            byte[] avatar = memService.getAvatar(memberId);
            
            // 如果沒有頭像，回傳 404
            if (avatar == null || avatar.length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            // 設定回應標頭
            return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")  // 根據實際圖片類型調整
                .header("Cache-Control", "max-age=3600")  // 快取 1 小時
                .body(avatar);
                
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
