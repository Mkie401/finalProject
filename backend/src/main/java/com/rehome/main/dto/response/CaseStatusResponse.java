package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class CaseStatusResponse {
    private String caseNumber;
    private Long memberId;
    private Long status;      // 1等待審核,2成功,3失敗
    private String rejectReason; // 退件原因
}
