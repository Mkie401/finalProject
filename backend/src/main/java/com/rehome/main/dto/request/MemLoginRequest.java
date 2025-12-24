package com.rehome.main.dto.request;

import lombok.Data;

@Data
public class MemLoginRequest {
    private String email;
    private String password;
    private String captcha;
    private String sessionId;
}