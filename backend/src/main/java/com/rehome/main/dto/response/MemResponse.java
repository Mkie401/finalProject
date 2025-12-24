package com.rehome.main.dto.response;
import java.util.Date;

import lombok.Data;

// 回傳給前端的會員資料
@Data
public class MemResponse {
    private int id;
    private String email;
    private String name;
    private String nickName;
    private boolean gender;
    private Date birthDate;
    private String phone;
} 