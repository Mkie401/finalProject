/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.dto.request;
import java.util.List;

import lombok.Data;

/**
 *
 * @author user
 */
@Data
public class PetinfoConDto {
    //寵物基本資訊
    private String petNickname;
    private String petGender;
    private String petSize;
    private Boolean petNeutered;
    private Long petLocationId;   
    private Long petDistrictId;
    private String petBreed;
 
    private Long petTypeId;
    private String petTypeOther;
    private String petAge;
    private Boolean petMicrochip;
    private String petMicrochipNumber;
 
    //送養範圍
    private List<Long> adoptCityIds;
    //補充說明
    private String additionalInfo;
    private String medicalInfo;

    //聯絡人資料
    private String contactName;
    private String contactPhone;
    private Boolean phoneDisplay;
    private String contactEmail;
    private Boolean emailDisplay;

    //領養要求
    private String followUp;
    private String familyConsent;
    private String ageLimit;
    private String adoptionRequirements;
    //照片
    private List<PetPhotoDto> photos;
 
}
