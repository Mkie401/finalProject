package com.rehome.main.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseContactResponseDTO {
    // 共用 送養、收容所
    private String name;
    private String tel;
    private String mail;
    private String addr;

    private Boolean isPhoneDisplay;
    private Boolean isEmailDisplay;
}
