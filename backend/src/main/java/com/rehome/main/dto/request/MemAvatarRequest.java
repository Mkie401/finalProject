package com.rehome.main.dto.request;

import lombok.Data;

// 會員頭像
@Data
public class MemAvatarRequest {
    private String avatar;
    private Integer memberId;
}
