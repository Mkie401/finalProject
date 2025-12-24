package com.rehome.main.dto.request;
import lombok.Data;

// 註冊信箱驗證
@Data
public class MemSendVerificationRequest {
    private String email;
}