package com.rehome.main.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.entity.MemOtpEntity;
import com.rehome.main.entity.PasswordResetTokenEntity;
import com.rehome.main.repository.MemOtpRepository;
import com.rehome.main.repository.PasswordResetTokenRepository;

@Service
public class MemEmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MemOtpRepository memOtpRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    // OTP 有效期限 10 分鐘
    private static final int OTP_EXPIRY_MINUTES = 10;

    // 產生六位數 OTP
    private String generateOtp(){
        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // 發送OTP
    @Transactional
    public void memSendConfirmMail(String email) {
        
        // 刪除舊的 OTP 記錄
        memOtpRepository.deleteByEmail(email);
        
        // 產生新的 OTP
        String otpCode = generateOtp();

        // 建立新的 OTP 實體
        MemOtpEntity memOtpEntity = new MemOtpEntity();
        memOtpEntity.setEmail(email);
        memOtpEntity.setOtpCode(otpCode);
        memOtpEntity.setCreatedAt(LocalDateTime.now());
        memOtpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        // 儲存到資料庫
        memOtpRepository.save(memOtpEntity);

        // 發送郵件
        SimpleMailMessage memMessage = new SimpleMailMessage();
        memMessage.setFrom("2025rehome@gmail.com");
        memMessage.setTo(email);
        memMessage.setSubject("ReHome信箱驗證碼");
        memMessage.setText("您好，您的Email驗證碼為 : " + otpCode + " ， 請於" + OTP_EXPIRY_MINUTES + "分鐘內使用此驗證碼完成驗證。");
        mailSender.send(memMessage);

    }

    // 驗證 OTP
    @Transactional
    public boolean memVerifyOtp(String email, String otpCode){

        // 查詢最新的驗證碼 OTP
        Optional<MemOtpEntity> optionalOtp = memOtpRepository.findLatestUnverifiedByEmail(email);

        // 若無驗證碼，則驗證失敗
        if(optionalOtp.isEmpty()){
            return false;
        }

        // 取得驗證碼實體
        MemOtpEntity memOtpEntity = optionalOtp.get();

        // 檢查是否過期
        if(LocalDateTime.now().isAfter(memOtpEntity.getExpiresAt())){
            return false;
        }

        // 檢查驗證碼是否正確
        if(!memOtpEntity.getOtpCode().equals(otpCode)){
            return false;
        }

        // 標記驗證碼為已驗證
        memOtpEntity.setVerified(true);
        memOtpRepository.save(memOtpEntity);
        return true;


    }

    // ========== 重設密碼相關方法 ==========
    
    // Token 有效期限 30 分鐘
    private static final int RESET_TOKEN_EXPIRY_MINUTES = 30;
    
    /**
     * 產生重設密碼的 Token
     * @return 隨機產生的 Token 字串
     */
    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        
        // 產生 32 位隨機英數字 Token
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 32; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return token.toString();
    }
    
    /**
     * 發送重設密碼郵件
     * @param email 使用者的電子郵件
     * @return 產生的 Token
     */
    @Transactional
    public String sendPasswordResetEmail(String email) {
        
        // 刪除該 Email 的舊 Token 記錄
        passwordResetTokenRepository.deleteByEmail(email);
        
        // 產生新的 Token
        String token = generateResetToken();
        
        // 建立新的 PasswordResetToken 實體
        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES));
        resetToken.setUsed(false);
        
        // 儲存到資料庫
        passwordResetTokenRepository.save(resetToken);
        
        // 建立重設密碼連結
        String resetLink = "http://localhost:8080/#resetpassword?token=" + token;
        
        // 發送郵件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2025rehome@gmail.com");
        message.setTo(email);
        message.setSubject("ReHome 重設密碼通知");
        message.setText(
            "您好，\n\n" +
            "您已申請重設密碼。請點擊以下連結進行密碼重設：\n\n" +
            resetLink + "\n\n" +
            "此連結將於 " + RESET_TOKEN_EXPIRY_MINUTES + " 分鐘後失效。\n\n" +
            "如果您未申請重設密碼，請忽略此郵件。\n\n" +
            "ReHome 團隊"
        );
        mailSender.send(message);
        
        return token;
    }
}
