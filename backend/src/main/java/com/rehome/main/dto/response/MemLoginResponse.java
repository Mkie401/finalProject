package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class MemLoginResponse {
    private Boolean success; // 登入是否成功
    private String message; // 返回訊息
    private String token; // 登入成功後的token
}
