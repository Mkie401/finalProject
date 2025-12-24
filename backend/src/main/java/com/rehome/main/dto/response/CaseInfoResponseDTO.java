package com.rehome.main.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseInfoResponseDTO {
    private Long id;
    private String caseNumber; // 案件編號、收容編號
    private LocalDateTime caseDateStart;

    private Boolean isFavorites;
    private Boolean isMissing;      // 走失協尋用
    private Boolean isPublic;       // 送養領養用
    private Boolean isOpen;         // 送養領養用

    private List<byte[]> photo;
}
