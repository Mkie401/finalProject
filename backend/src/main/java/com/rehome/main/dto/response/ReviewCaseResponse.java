package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class ReviewCaseResponse {
    private String caseNumber;
    private Long status; // 案件狀態 
    private String caseType;   // 案件種類
    private String caseStartDate; // 立案時間
    private String account;
    private String description;

    // 建構子 (方便 Service 轉換用)
    public ReviewCaseResponse(String caseNumber, Long status, String caseType, String caseStartDate,String account, String description ) {
        this.caseNumber = caseNumber;
        this.status = status;
        this.caseType = caseType;
        this.caseStartDate = caseStartDate;
        this.account = account;
        this.description = description;
    }

}
