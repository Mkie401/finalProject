package com.rehome.main.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceFormResponse {
    private Long id;
    private String questionTypeName; // 轉換後的類型名稱
    private String questionTitle;
    private String questionInfo;
    private String cname;
    private String cmail;
    private Date createdTime;
    private String status;
}