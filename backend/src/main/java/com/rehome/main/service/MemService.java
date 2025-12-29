package com.rehome.main.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.MemLoginRequest;
import com.rehome.main.dto.request.MemRequest;
import com.rehome.main.dto.response.MemLoginResponse;
import com.rehome.main.dto.response.MemResponse;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.PasswordResetTokenEntity;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.repository.PasswordResetTokenRepository;
import com.rehome.main.utils.MemJwtUtil;

@Service
public class MemService {
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemJwtUtil memJwtUtil;

    @Autowired
    private MemCaptchaService memCaptchaService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MemEmailService memEmailService;
    
    // 會員註冊
    public MemResponse register(MemRequest request) {

        // 檢查 email 是否已存在
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("此 Email 已被註冊");
        }
        
        // 建立 Entity 物件
        Member entity = new Member();
        entity.setEmail(request.getEmail());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword())); // 加密密碼
        entity.setName(request.getName());
        entity.setGender(request.isGender());
        if (request.getBirthDate() != null) {
            entity.setBirthDate(new java.sql.Date(request.getBirthDate().getTime()));
        }
        entity.setPhone(request.getPhone());
        
        // 儲存到資料庫
        Member savedEntity = memberRepository.save(entity);
        
        // 轉換成 Response 回傳
        return convertToResponse(savedEntity);
    }
    
    // Entity 轉 Response DTO
    private MemResponse convertToResponse(Member entity) {
        MemResponse response = new MemResponse();
        response.setId(entity.getId().intValue());
        response.setEmail(entity.getEmail());
        response.setName(entity.getName());
        response.setNickName(entity.getNickName());
        response.setGender(entity.getGender());
        response.setBirthDate(entity.getBirthDate());
        response.setPhone(entity.getPhone());
        return response;
    }

    // 根據會員ID查詢資料
    public MemResponse getMemberById(Integer memberId) {

        Member entity = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        return convertToResponse(entity);
    }

    // 基本資料 - 更新暱稱
    public MemResponse updateNickName(Integer memberId, String newNickName) {

        Member entity = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        entity.setNickName(newNickName);
        Member updatedEntity = memberRepository.save(entity);

        return convertToResponse(updatedEntity);
    }

    // 基本資料 - 更新密碼
    public void updatePassword(Integer memberId, String oldPassword, String newPassword) {

        Member entity = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        
        // 比對舊密碼
        if(!passwordEncoder.matches(oldPassword, entity.getPasswordHash())){
            throw new RuntimeException("舊密碼不正確");
        }

        // 更新加密
        entity.setPasswordHash(passwordEncoder.encode(newPassword));

        // 儲存
        memberRepository.save(entity);
    }

    // 登入
    public MemLoginResponse login(MemLoginRequest request) {

        MemLoginResponse response = new MemLoginResponse();

        // 驗證碼檢查
        if(!memCaptchaService.verifyCaptcha(request.getSessionId(), request.getCaptcha())){
            response.setSuccess(false);
            response.setMessage("驗證碼錯誤");
            return response;
        }

        // 根據 email 查找會員
        Member member = memberRepository.findByEmail(request.getEmail()).orElse(null);

        // 檢查會員是否存在
        if(member == null) {
            response.setSuccess(false);
            response.setMessage("會員不存在");
            return response;
        }

        // 比對密碼
        if(!passwordEncoder.matches(request.getPassword(), member.getPasswordHash())) {
            response.setSuccess(false);
            response.setMessage("密碼錯誤");
            return response;
        }

        // 登入成功
        response.setSuccess(true);
        response.setMessage("登入成功");
        response.setToken(memJwtUtil.generateToken(member.getEmail(), member.getId()));

        return response;
    }

    // ========== 重設密碼相關方法 ==========
    
    /**
     * 處理忘記密碼請求
     * @param email 使用者的電子郵件
     * @throws RuntimeException 如果會員不存在
     */
    public void requestPasswordReset(String email) {
        
        // 檢查會員是否存在
        if (!memberRepository.existsByEmail(email)) {
            throw new RuntimeException("此 Email 尚未註冊");
        }
        
        // 發送重設密碼郵件
        memEmailService.sendPasswordResetEmail(email);
    }
    
    /**
     * 驗證重設密碼 Token 是否有效
     * @param token 重設密碼的 Token
     * @return true 表示 Token 有效，false 表示無效或已過期
     */
    public boolean verifyResetToken(String token) {
        
        // 查詢 Token
        PasswordResetTokenEntity resetToken = 
            passwordResetTokenRepository.findByToken(token).orElse(null);
        // Token 不存在
        if (resetToken == null) {
            return false;
        }
        
        // Token 已被使用
        if (resetToken.isUsed()) {
            return false;
        }
        
        // Token 已過期
        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 重設密碼
     * @param token 重設密碼的 Token
     * @param newPassword 新密碼
     * @throws RuntimeException 如果 Token 無效或已過期
     */
    public void resetPassword(String token, String newPassword) {
        
        // 查詢 Token
        PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("無效的重設連結"));
        
        // 檢查是否已使用
        if (resetToken.isUsed()) {
            throw new RuntimeException("此重設連結已被使用");
        }
        
        // 檢查是否過期
        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new RuntimeException("重設連結已過期，請重新申請");
        }
        
        // 查詢會員
        Member member = memberRepository.findByEmail(resetToken.getEmail()).orElse(null);
        if (member == null) {
            throw new RuntimeException("會員不存在");
        }
        
        // 更新密碼
        member.setPasswordHash(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        
        // 標記 Token 為已使用
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // 儲存會員頭像
    public void saveAvatar(Integer memberId, String base64Date) {
        // 查詢會員
        Member member = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        try {
            // 處理 Base64 字串
            String base64Image = base64Date;
            if(base64Date.contains(",")){
                base64Image = base64Date.split(",")[1];
            }

            // 解碼 Base64 字串
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);

            // 檢查檔案大小 (限制在 2MB 以內)
            if(imageBytes.length > 2 * 1024 * 1024) {
                throw new RuntimeException("頭像檔案大小不能超過 2MB");
            }

            // 儲存頭像
            member.setIcon(imageBytes);
            memberRepository.save(member);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Base64 解碼失敗，請確認圖片格式正確");
        } catch (RuntimeException e) {
            // 重新拋出 RuntimeException（包括檔案大小限制）
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("頭像解碼失敗");
        }
    }

    // 取得會員頭像
    public byte[] getAvatar(Integer memberId) {
        // 查詢會員
        Member member = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        return member.getIcon();
    }
}
