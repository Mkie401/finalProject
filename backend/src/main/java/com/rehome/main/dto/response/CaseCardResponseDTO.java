package com.rehome.main.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CaseCardResponseDTO {
    // 沒用到的欄位就是NULL
    // 案件基本資料
    private Long id;
    private String caseNumber;
    private LocalDateTime caseDateStart;

    // 狀態
    private Boolean isFavorites;
    private Boolean isPublic;       // 送養領養用
    private Boolean isOpen;         // 送養領養用

    // 圖片
    private byte[] photo;

    // 卡片資料
    private String petName;
    private String species;
    private String breed;
    private String size;
    private String region;          // 送養領養用
    
    private LocalDateTime lostDate; // 走失協尋用
    private String lostRegion;      // 走失協尋用
}
