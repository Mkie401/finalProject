package com.rehome.main.dto.request;
import java.util.Date;

import lombok.Data;

// 前端送來的會員註冊資料
@Data
public class MemRequest {
    private String email;
    private String password;
    private String name;
    private boolean gender;
    private Date birthDate;
    private String phone;
}