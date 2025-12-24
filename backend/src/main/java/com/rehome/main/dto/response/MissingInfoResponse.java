package com.rehome.main.dto.response;

import lombok.Data;

@Data
public class MissingInfoResponse {
    private Long memberId;
    private String petName;
    private String petGender;
    private String missingDate; 
    private String species; //種類
    private String petBreed; //品種
    private String missingDistrict; //走失地點
    private String caseStatus; //狀態
    private String caseNumber; //案號
    private String petImg;
}
